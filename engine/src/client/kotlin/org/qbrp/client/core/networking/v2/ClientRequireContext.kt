package org.qbrp.client.core.networking.v2

import net.minecraft.network.PacketByteBuf

interface ClientRequireContext {
    fun post(b: PacketByteBuf)
}