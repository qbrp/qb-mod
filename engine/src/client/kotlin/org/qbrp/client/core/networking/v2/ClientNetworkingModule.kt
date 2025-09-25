package org.qbrp.client.core.networking.v2

import io.netty.buffer.Unpooled
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.network.PacketByteBuf
import org.koin.dsl.module
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.networking.NetworkingModule
import org.qbrp.main.core.networking.Packet
import org.qbrp.main.core.networking.sender.PacketConstructor
import org.qbrp.main.core.networking.PacketListener
import org.qbrp.main.core.networking.PacketListenerActivator
import org.qbrp.main.core.networking.acknowledge.AcknowledgePacket
import org.qbrp.main.core.networking.acknowledge.AcknowledgePacketListener
import org.qbrp.main.core.networking.require.RequestId
import org.qbrp.main.core.networking.require.RequirePacket
import org.qbrp.main.core.networking.require.RequirePacketListener
import org.qbrp.main.core.networking.require.RequireId
import org.qbrp.main.core.networking.toIdentifier
import kotlin.concurrent.fixedRateTimer

typealias RequireHandler = (ClientRequireContext) -> Unit
typealias ClientPacketListenerManager = PacketListenerActivator<ClientContext>

@Autoload(priority = Priorities.HIGHEST, env = EnvType.CLIENT)
class ClientNetworkingModule: EngineModule("client-networking"), ClientPacketListenerManager, ClientPacketSender, AcknowledgePacketListener, RequirePacketListener, RequirePacketHandlerManager {
    private val handledPackets: LinkedHashSet<Long> = LinkedHashSet()
    private val requireHandlers: MutableMap<RequireId, RequireHandler> = mutableMapOf()
    private val clearDuration: Int = 5 * 60 * 1000
    private val client get() = MinecraftClient.getInstance()
    private val lock = Any()

    override fun getKoinModule() = module {
        single<ClientPacketSender> { this@ClientNetworkingModule }
        single<ClientPacketListenerManager> { this@ClientNetworkingModule }
        single<RequirePacketHandlerManager> { this@ClientNetworkingModule }
    }

    override fun onEnable() {
        run(NetworkingModule.ACKNOWLEDGE_REQUEST_CHANNEL, this, { buf, ctx -> AcknowledgePacket(buf) })
        run(NetworkingModule.REQUIRE_CHANNEL, this, { buf, ctx -> RequirePacket(buf) })
        fixedRateTimer("ClearHandledPackets", true, 0L, clearDuration.toLong()) {
            MinecraftClient.getInstance().execute { clearHandledPackets() }
        }
    }

    override fun send(p: Packet<*>, channelName: String) {
        val buf = PacketByteBuf(Unpooled.buffer())
        p.write(buf)
        ClientPlayNetworking.send(toIdentifier(channelName), buf)
    }

    override fun run(
        channelName: String,
        listener: PacketListener,
        constructor: PacketConstructor<ClientContext>
    ) {
        ClientPlayNetworking.registerGlobalReceiver(toIdentifier(channelName)) { client, handler, buf, responseSender ->
            val id = buf.readLong()
            synchronized(lock) { handledPackets.add(id) }
            val packet = constructor.build(buf, ClientContext(client, handler)) as Packet<PacketListener>
            client.execute { listener.onPacket(packet) }
        }
    }

    private fun clearHandledPackets(multipier: Double = 0.7) = synchronized(lock) {
         val toRemove = handledPackets.take((handledPackets.size * multipier).toInt())
        handledPackets.removeAll(toRemove)
    }

    override fun onAcknowledge(id: Long) {
        send(AcknowledgePacket(id), NetworkingModule.ACKNOWLEDGE_CHANNEL)
    }

    override fun onRequire(id: RequireId, requestId: RequestId) {
        val ctx = object : ClientRequireContext {
            override fun post(b: PacketByteBuf) {
                b.writeInt(requestId.value)
                ClientPlayNetworking.send(toIdentifier(NetworkingModule.POST_REQUIRE_CHANNEL), b)
            }
        }
        requireHandlers[id]?.invoke(ctx) ?: throw IllegalArgumentException()
    }

    override fun registerHandler(id: RequireId, handler: RequireHandler) {
        requireHandlers[id] = handler
    }
}