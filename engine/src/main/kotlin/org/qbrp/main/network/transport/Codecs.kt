package org.qbrp.main.network.transport

import io.netty.buffer.Unpooled
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import net.minecraft.network.PacketByteBuf

sealed class PacketCodec<P : Packet> {
    class Kotlinx<P : Packet>(val serializer: KSerializer<P>) : PacketCodec<P>()
    class Binary<P : Packet>(
        val deserializer: PacketByteBuf.() -> P,
        val serializer: PacketByteBuf.(P) -> Unit
    ) : PacketCodec<P>()
}

@OptIn(ExperimentalSerializationApi::class)
fun <P : Packet> deserializePacket(buf: PacketByteBuf, codec: PacketCodec<P>): P {
    return when (codec) {
        is PacketCodec.Binary<P> -> {
            codec.deserializer.invoke(buf)
        }

        is PacketCodec.Kotlinx<P> -> {
            return ProtoBuf.decodeFromByteArray(codec.serializer, buf.readByteArray())
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
fun <P : Packet> serializePacket(buf: PacketByteBuf, packet: P, codec: PacketCodec<P>) {
    when (codec) {
        is PacketCodec.Binary<P> -> {
            codec.serializer.invoke(buf, packet)
        }

        is PacketCodec.Kotlinx<P> -> {
            buf.writeByteArray(ProtoBuf.encodeToByteArray(codec.serializer, packet))
        }
    }
}

@Serializable
data class TestPacket(val value: Int) : Packet

fun main() {
    val codec = PacketCodec.Kotlinx(TestPacket.serializer())
    val buf = PacketByteBuf(Unpooled.buffer())
    serializePacket(buf, TestPacket(12), codec)
    println(deserializePacket(buf, codec).value)
}