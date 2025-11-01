package org.qbrp.main.network.transport

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import net.minecraft.util.Identifier
import org.qbrp.main.core.Core
import kotlin.reflect.KClass

interface NetworkChannel<P : Packet> {
    val channelName: String
    val codec: PacketCodec<P>
}

data class NetworkChannelImpl<P : Packet>(
    override val channelName: String,
    override val codec: PacketCodec<P>,
    val packetClass: KClass<P>,
) : NetworkChannel<P>

@OptIn(InternalSerializationApi::class)
inline fun <reified P : Packet> NetworkChannel(
    id: String = P::class.channelName,
    codec: PacketCodec<P> = PacketCodec.Kotlinx(P::class.serializer())
): NetworkChannel<P> = NetworkChannelImpl(id, codec, P::class)

inline fun <reified P : Packet> NetworkChannel(
    id: String,
    serializer: KSerializer<P>,
) = NetworkChannel<P>(id, PacketCodec.Kotlinx<P>(serializer))

val KClass<out Packet>.channelName
    get() = simpleName?.lowercase() ?: error("Unknown packet class name")

val NetworkChannel<*>.minecraftIdentifier
    get() = Identifier(Core.MOD_ID, channelName)