package core.networking

import net.minecraft.network.PacketByteBuf
import org.qbrp.main.core.networking.NetworkContext
import org.qbrp.main.core.networking.Packet
import org.qbrp.main.core.networking.PacketListener
import org.qbrp.main.core.networking.PacketListenerActivator
import org.qbrp.main.core.networking.sender.PacketConstructor

abstract class PredictedPacketListenerActivator<C: NetworkContext>: PacketListenerActivator<C> {
    inner class Listener(val channelName: String, val listener: PacketListener, val constructor: PacketConstructor<C>)
    private val receivers: MutableMap<String, Listener> = mutableMapOf()

    override fun run(
        channelName: String,
        listener: PacketListener,
        constructor: PacketConstructor<C>
    ) {
        receivers[channelName] = Listener(channelName, listener, constructor)
    }

    fun on(buf: PacketByteBuf, channelName: String) {
        val receiver = receivers[channelName] ?: throw IllegalArgumentException("Listener not found")
        val packet = receiver.constructor.build(buf, newContext()) as Packet<PacketListener>
        receiver.listener.onPacket(packet)
    }

    abstract fun newContext(): C
}