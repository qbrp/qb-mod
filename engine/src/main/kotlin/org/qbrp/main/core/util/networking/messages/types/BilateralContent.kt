package org.qbrp.main.core.util.networking.messages.types

import net.minecraft.network.PacketByteBuf
import java.util.UUID

abstract class BilateralContent: ReceiveContent, SendContent {
    override var messageId: String = UUID.randomUUID().toString()

    override fun convert(buf: PacketByteBuf): BilateralContent {
        messageId = buf.readString()
        return this
    }

    override fun write(buf: PacketByteBuf): PacketByteBuf {
        buf.writeString(messageId)
        return buf
    }
}

