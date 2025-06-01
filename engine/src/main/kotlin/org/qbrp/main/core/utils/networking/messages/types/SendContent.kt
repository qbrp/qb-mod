package org.qbrp.main.core.utils.networking.messages.types

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.network.PacketByteBuf

interface SendContent: Content {
    fun write(buf: PacketByteBuf = PacketByteBufs.create()): PacketByteBuf
    fun setData(data: Any)
}