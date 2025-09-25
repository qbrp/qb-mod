package org.qbrp.main.core.networking

import net.minecraft.network.PacketByteBuf

interface Packet<T> {
    fun write(buf: PacketByteBuf)
    fun apply(listener: T)
}