package org.qbrp.system.networking.messages.types

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.network.PacketByteBuf
import java.util.UUID

class BooleanContent(var bool: Boolean? = false): BilateralContent() {

    override fun convert(buf: PacketByteBuf): BilateralContent {
        super.convert(buf)
        bool = buf.readBoolean()
        return this
    }

    override fun getData(): Any {
        return bool!!
    }

    override fun write(buf: PacketByteBuf): PacketByteBuf {
        super.write(buf)
        buf.writeBoolean(bool!!)
        return buf
    }
}

