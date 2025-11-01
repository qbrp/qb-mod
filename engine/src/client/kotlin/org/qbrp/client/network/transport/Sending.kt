package org.qbrp.client.network.transport

import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.network.PacketByteBuf
import org.qbrp.main.core.util.IDGenerator
import org.qbrp.main.network.transport.NetworkChannel
import org.qbrp.main.network.transport.Packet
import org.qbrp.main.network.transport.minecraftIdentifier
import org.qbrp.main.network.transport.serializePacket

fun <P : Packet> sendC2SPacket(packet: P, channel: NetworkChannel<P>, id: Long = IDGenerator.nextId()) {
    val buf = PacketByteBuf(Unpooled.buffer())
    buf.writeLong(id)
    serializePacket(buf, packet, channel.codec)
    ClientPlayNetworking.send(channel.minecraftIdentifier, buf)
}

fun <P : Packet> NetworkChannel<P>.sendC2SPacket(packet: P, id: Long = IDGenerator.nextId()) {
    sendC2SPacket(packet, this, id)
}