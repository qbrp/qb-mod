package org.qbrp.main.core.networking.sender

import net.minecraft.network.PacketByteBuf
import org.qbrp.main.core.networking.NetworkContext
import org.qbrp.main.core.networking.Packet

fun interface PacketConstructor<Ctx: NetworkContext> {
    fun build(buf: PacketByteBuf, ctx: Ctx): Packet<*>
}