package org.qbrp.main.network

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.fabricmc.player.PlayerReference
import org.qbrp.main.core.fabricmc.player.asReference
import org.qbrp.main.core.util.IDGenerator
import org.qbrp.main.network.player.disconnect
import org.qbrp.main.network.transport.AcknowledgeProgram
import org.qbrp.main.network.transport.NetworkChannel
import org.qbrp.main.network.transport.Packet
import org.qbrp.main.network.transport.PacketSendTask
import org.qbrp.main.network.transport.ServerDependencies
import org.qbrp.main.network.transport.ServerNetworkContext
import org.qbrp.main.network.transport.ServerPacketHandler
import org.qbrp.main.network.transport.ServerPacketSendTask
import org.qbrp.main.network.transport.deserializePacket
import org.qbrp.main.network.transport.minecraftIdentifier
import org.qbrp.main.network.transport.reason
import org.qbrp.main.network.transport.sendS2CPacketMc
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.concurrent.thread

class ServerPacketTransportProgram(
    private val acknowledgeProgram: AcknowledgeProgram,
    private val dependencies: ServerDependencies,
    private val sessionManager: ServerConnectionManager,
    private val server: MinecraftServer
) {
    private val queue = ConcurrentLinkedQueue<Runnable>()

    fun runTransportThread() {
        thread(name = "[Engine] Packet sending", isDaemon = true) {
            while (true) {
                try {
                    queue.poll()?.run()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun <P : Packet> registerServerReceiver(channel: NetworkChannel<P>, handler: ServerPacketHandler<P>) {
        ServerPlayNetworking.registerGlobalReceiver(channel.minecraftIdentifier) { server, entity, handler, buf, sender ->
            val id = buf.readLong()
            val session = sessionManager.getSession(handler)
            if (sessionManager.disconnectIfTimeoutsExpires(session)) return@registerGlobalReceiver
            val result = runCatching { deserializePacket<P>(buf, channel.codec) }
            server.execute {
                var fail: Throwable? = null
                val plr = entity.asReference()
                val packet = result
                    .onFailure { error -> fail = error }
                    .getOrNull()

                with(ServerNetworkContext(dependencies, session)) {
                    val result = runCatching { packet?.handler(this) }
                    result.onFailure { error -> fail = error }
                }

                if (fail != null) {
                    fail.printStackTrace()
                    sessionManager.disconnect(session, reason(fail))
                }
            }
        }
    }

    fun <P : Packet> taskS2CPacket(
        packet: P,
        channel: NetworkChannel<P>,
        player: PlayerReference
    ): PacketSendTask {
        val id = IDGenerator.nextId()
        return ServerPacketSendTask(
            id,
            packet,
            channel,
            this@ServerPacketTransportProgram,
            acknowledgeProgram,
            player.entity!!
        )
    }

    fun <P : Packet> sendS2CPacket(
        packet: P,
        channel: NetworkChannel<P>,
        player: PlayerReference
    ) {
        val entity = player.entity ?: return
        queueSendTask(entity, packet, channel)
    }

    internal fun <P : Packet> queueSendTask(
        player: ServerPlayerEntity,
        packet: P,
        channel: NetworkChannel<P>,
        id: Long = IDGenerator.nextId()
    ) = queue.add {
        if (player.isDisconnected) return@add
        sendS2CPacketMc(player, packet, channel.codec, channel.minecraftIdentifier, id)
    }

    val PlayerReference.entity get() = server.playerManager.getPlayer(uuid.value)
}