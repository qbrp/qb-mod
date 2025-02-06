package org.qbrp.system.networking.messages.types

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.network.PacketByteBuf

interface SendContent: Content {
    fun write(buf: PacketByteBuf = PacketByteBufs.create()): PacketByteBuf
}