package org.qbrp.main.core.util.networking.messages.types

import net.minecraft.network.PacketByteBuf
import java.util.UUID

class ReadBuf<T: Any>(
    private val convertStrategy: ((PacketByteBuf) -> T)? = null,
) : ReceiveContent {
    override val messageId: String = UUID.randomUUID().toString()
    private lateinit var value: T

    override fun convert(buf: PacketByteBuf): Content {
        value = convertStrategy!!(buf)
        return this
    }

    override fun getData(): T = value
}