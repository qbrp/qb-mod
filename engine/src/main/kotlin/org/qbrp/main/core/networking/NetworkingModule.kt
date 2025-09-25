package org.qbrp.main.core.networking

import io.netty.buffer.Unpooled
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.time.delay
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.dsl.module
import org.qbrp.main.core.game.IDGenerator
import org.qbrp.main.core.mc.context.withMinecraftContext
import org.qbrp.main.core.mc.player.model.PlayerReference
import org.qbrp.main.core.mc.player.disconnect
import org.qbrp.main.core.mc.player.getEntity
import org.qbrp.main.core.mc.player.service.MinecraftPlayerManager
import org.qbrp.main.core.mc.player.service.snapshot.AbstractSnapshotService
import org.qbrp.main.core.mc.player.service.snapshot.ServerPlayerSnapshotService
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.networking.acknowledge.AcknowledgePacket
import org.qbrp.main.core.networking.acknowledge.AcknowledgePacketListener
import org.qbrp.main.core.networking.acknowledge.PacketSendTask
import org.qbrp.main.core.networking.require.RequireManager
import org.qbrp.main.core.networking.sender.PacketConstructor
import org.qbrp.main.core.networking.sender.ServerPacketSender
import org.qbrp.main.core.utils.format.Format.formatMm
import org.qbrp.main.engine.players.model.engineOrNull
import org.qbrp.main.engine.players.model.networking.DisconnectReason
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.concurrent.thread
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlin.time.toJavaDuration

@Autoload
class NetworkingModule: EngineModule("networking"), ServerPacketSender, PacketListenerActivator<ServerNetworkContext>, AcknowledgePacketListener {
    companion object {
        const val ACKNOWLEDGE_CHANNEL = "acknowledge"
        const val ACKNOWLEDGE_REQUEST_CHANNEL = "acknowledge_request"
        const val REQUIRE_CHANNEL = "require"
        const val POST_REQUIRE_CHANNEL = "post_require"
    }
    private val waitAcknowledgePackets: MutableMap<Long, Channel<Unit>> = mutableMapOf()
    private val queue: Queue<Runnable> = ConcurrentLinkedQueue()
    private val playerManager: MinecraftPlayerManager by inject()
    private val snapshotService: AbstractSnapshotService by lazy { ServerPlayerSnapshotService(get(), get(), get()) }
    override fun getKoinModule() = module {
        single<ServerPacketSender> { this@NetworkingModule }
        single<PacketListenerActivator<ServerNetworkContext>> { this@NetworkingModule }
        single { RequireManager() }
    }

    override fun onEnable() {
        val postRequirePacketListener: RequireManager by inject()
        run(ACKNOWLEDGE_CHANNEL, this, { buf, ctx -> AcknowledgePacket(buf) })

        ServerPlayNetworking.registerGlobalReceiver(toIdentifier(POST_REQUIRE_CHANNEL)) { server, entity, handler, buf, sender ->
            postRequirePacketListener.onPacket(
                buf,
                ServerNetworkContext(
                    server,
                    snapshotService.lazySnapshotPlayer(entity),
                    handler
                )
            )
        }

        thread(name = "EnginePacketSending") {
            while (true) { while(queue.isNotEmpty()) { queue.poll().run() } }
        }
    }

    override fun sendImmediately(
        packet: Packet<*>,
        channelName: String,
        player: PlayerReference
    ) {
        sendImmediately(packet, channelName, player.getEntity(playerManager))
    }

    override fun sendImmediately(
        packet: Packet<*>,
        channelName: String,
        player: ServerPlayerEntity
    ) {
        queue.add {
            val buf = PacketByteBuf(Unpooled.buffer())
            buf.writeLong(IDGenerator.nextId())
            packet.write(buf)
            ServerPlayNetworking.send(player, toIdentifier(channelName), buf)
        }
    }

    override fun send(packet: Packet<*>, channelName: String, player: PlayerReference): PacketSendTask {
        return PacketSendTaskImpl(player, packet, channelName)
    }

    override fun run(
        channelName: String,
        listener: PacketListener,
        constructor: PacketConstructor<ServerNetworkContext>
    ) {
        ServerPlayNetworking.registerGlobalReceiver(toIdentifier(channelName)) { server, entity, handler, buf, sender ->
            val packet = constructor.build(buf, ServerNetworkContext(server, snapshotService.lazySnapshotPlayer(entity), handler)) as Packet<PacketListener>
            server.execute {
                val plr = entity.engineOrNull()
                withMinecraftContext {
                    try {
                        if (plr != null) {
                            listener.onPacket(packet, plr)
                        } else {
                            listener.onPacket(packet)
                        }
                    } catch (e: Exception) {
                        plr?.disconnect(DisconnectReason("Ошибка обработки пакета: ${e.message}")) ?: entity.networkHandler.disconnect(Text.of(e.message))
                        e.printStackTrace()
                    }
                }

            }
        }
    }

    override fun onAcknowledge(id: Long) {
        waitAcknowledgePackets[id]?.trySend(Unit)
    }

    inner class PacketSendTaskImpl(
        private val player: PlayerReference,
        val packet: Packet<*>,
        val channelName: String,
        override val id: Long = IDGenerator.nextId()
    ): PacketSendTask {
        var log: Boolean = false
        var logName: String = ""

        override suspend fun waitAcknowledge(retryAttempts: Int, retryTime: Int) = coroutineScope {
            val receiveChannel = Channel<Unit>(capacity = 1)
            waitAcknowledgePackets[id] = receiveChannel
            if (log) println("Ожидание подтверждения $logName: $id")

            val senderJob = async {
                var attempts = 0
                while (attempts < retryAttempts) {
                    sendAcknowledgeRequest(id)
                    delay(
                        retryTime.toLong().toDuration(DurationUnit.MILLISECONDS).toJavaDuration()
                    )  // пауза между попытками
                    attempts++
                }
                withMinecraftContext { player.disconnect(DisconnectReason("Пакет не подтвержден: $id")) }
                false
            }

            val receiverJob = async {
                receiveChannel.receive() // ждём подтверждения
                if (log) println("Пакет подтвержден $logName: $id")
            }

            try {
                select<Unit> {
                    senderJob.onAwait { senderJob.getCompleted() } // если sender бросит исключение
                    receiverJob.onAwait { receiverJob.getCompleted() } // если пришло подтверждение
                }
            } finally {
                waitAcknowledgePackets.remove(id)
                receiveChannel.close()
                senderJob.cancel()
                receiverJob.cancel()
            }
        }

        override suspend fun log(name: String): PacketSendTask {
            logName = name
            log = true
            return this
        }

        override fun queue(): PacketSendTask {
            queue.add {
                val buf = PacketByteBuf(Unpooled.buffer())
                buf.writeLong(id)
                packet.write(buf)
                ServerPlayNetworking.send(player.getEntity(playerManager), toIdentifier(channelName), buf)
                if (log) println("Отправлен ${packet::class.simpleName} (${logName}) : $id")
            }
            return this
        }

        private fun sendAcknowledgeRequest(id: Long) {
            sendImmediately(AcknowledgePacket(id), ACKNOWLEDGE_REQUEST_CHANNEL, player)
        }
    }
}