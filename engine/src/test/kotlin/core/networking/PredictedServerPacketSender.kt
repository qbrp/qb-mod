package core.networking

import io.netty.buffer.Unpooled
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.game.IDGenerator
import org.qbrp.main.core.mc.player.model.PlayerReference
import org.qbrp.main.core.mc.player.asReference
import org.qbrp.main.core.networking.Packet
import org.qbrp.main.core.networking.ServerNetworkContext
import org.qbrp.main.core.networking.acknowledge.PacketSendTask
import org.qbrp.main.core.networking.sender.ServerPacketSender

typealias ServerPacketListenerManager = PredictedPacketListenerActivator<ServerNetworkContext>

class PredictedServerPacketSender(private val playerListeners: MutableMap<PlayerReference, ServerPacketListenerManager> = mutableMapOf()): ServerPacketSender {
    constructor(player: PlayerReference, packetListenerManager: ServerPacketListenerManager) : this(mutableMapOf(player to packetListenerManager))

    override fun sendImmediately(
        packet: Packet<*>,
        channelName: String,
        player: PlayerReference
    ) {
        val buf = PacketByteBuf(Unpooled.buffer())
        (playerListeners[player] ?: throw IllegalArgumentException("Listener not found")).on(buf, channelName)
    }

    override fun sendImmediately(
        packet: Packet<*>,
        channelName: String,
        player: ServerPlayerEntity
    ) {
        sendImmediately(packet, channelName, player.asReference())
    }

    override fun send(
        packet: Packet<*>,
        channelName: String,
        player: PlayerReference
    ): PacketSendTask {
        return PacketSendTaskImpl(packet, player, channelName)
    }

    inner class PacketSendTaskImpl(private val packet: Packet<*>,
                                   private val player: PlayerReference,
                                   private val channelName: String,
                                   override val id: Long = IDGenerator.nextId()): PacketSendTask {
        override suspend fun waitAcknowledge(retryAttempts: Int, retryTime: Int) {}

        override suspend fun log(name: String): PacketSendTask {
            println("Отправлан пакет $name")
            return this
        }

        override fun queue(): PacketSendTask {
            sendImmediately(packet, channelName, player)
            return this
        }
    }
}