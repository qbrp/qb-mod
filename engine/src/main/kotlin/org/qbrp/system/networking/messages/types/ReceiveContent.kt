package org.qbrp.system.networking.messages.types

import net.minecraft.network.PacketByteBuf

interface ReceiveContent: Content {
    fun convert(buf: PacketByteBuf): Content
    fun getData(): Any
}