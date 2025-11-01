package org.qbrp.main.core.util.networking.messages.types

import net.minecraft.network.PacketByteBuf
import java.util.UUID

class WriteBuf(
    private val writeStrategy: ((PacketByteBuf) -> Unit)? = null,
) : SendContent {
    override val messageId: String = UUID.randomUUID().toString()

    override fun write(buf: PacketByteBuf): PacketByteBuf {
        buf.writeString(messageId)
        writeStrategy!!(buf)
        return buf
    }

    override fun setData(data: Any) = throw NotImplementedError()
}