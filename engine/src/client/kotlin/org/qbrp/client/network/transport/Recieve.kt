package org.qbrp.client.network.transport

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.TitleScreen
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
import org.qbrp.main.core.util.FixedSizeList
import org.qbrp.main.core.util.math.engineWorldVec3
import org.qbrp.main.network.transport.ACKNOWLEDGE_CONFIRM_CHANNEL
import org.qbrp.main.network.transport.ACKNOWLEDGE_REQUEST_CHANNEL
import org.qbrp.main.network.transport.AcknowledgePacket
import org.qbrp.main.network.transport.NetworkChannel
import org.qbrp.main.network.transport.NetworkContext
import org.qbrp.main.network.transport.Packet
import org.qbrp.main.network.transport.PacketHandler
import org.qbrp.main.network.transport.deserializePacket
import org.qbrp.main.network.transport.minecraftIdentifier
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.concurrent.fixedRateTimer

typealias ClientPacketHandler<P> = PacketHandler<ClientNetworkContext, P>

data class ClientNetworkContext(val client: MinecraftClient) : NetworkContext {
    val clientPlayerPosition get() = client.player?.pos?.engineWorldVec3()
}

class ClientPacketTransportProgram(
    val packetHistory: FixedSizeList<Long> = FixedSizeList(3000)
) {
    fun <P : Packet> registerClientReceiver(channel: NetworkChannel<P>, handler: ClientPacketHandler<P>) {
        ClientPlayNetworking.registerGlobalReceiver(channel.minecraftIdentifier) { client, handler, buf, responseSender ->
            val id = buf.readLong()
            packetHistory.add(id)
            val packet = deserializePacket(buf, channel.codec)
            client.execute {
                if (client.world == null) return@execute
                try {
                    val ctx = ClientNetworkContext(client)
                    with(ctx) {
                        packet.handler(ctx)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    val titleScreen = TitleScreen()
                    client.disconnect((MultiplayerScreen(titleScreen)))
                }
            }
        }
    }
}

private data class PendingPacket(val id: Long, var acknowledges: Int = 0)

fun ClientPacketTransportProgram.runAcknowledgeListeners() {
    val pendingPackets = CopyOnWriteArrayList<PendingPacket>()

    ACKNOWLEDGE_REQUEST_CHANNEL.registerClientReceiver {
        val acknowledged = tryAcknowledge(id)
        if (!acknowledged) pendingPackets.add(PendingPacket(id))
    }

    fixedRateTimer("Engine Acknowledge Packet Responding", daemon = true, period = 20L) {
        for (packet in pendingPackets) {
            val acknowledged = tryAcknowledge(packet.id)
            packet.acknowledges++
            if (acknowledged || packet.acknowledges > 20) {
                pendingPackets.remove(packet)
            }
        }
    }
}

fun ClientPacketTransportProgram.tryAcknowledge(id: Long): Boolean {
    return if (!packetHistory.contains(id)) {
        false
    } else {
        sendC2SPacket(AcknowledgePacket(id), ACKNOWLEDGE_CONFIRM_CHANNEL)
        true
    }
}