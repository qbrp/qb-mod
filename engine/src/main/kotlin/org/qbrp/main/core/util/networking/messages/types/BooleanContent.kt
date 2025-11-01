package org.qbrp.main.core.util.networking.messages.types

import net.minecraft.network.PacketByteBuf

class BooleanContent(var bool: Boolean? = false): BilateralContent() {

    override fun convert(buf: PacketByteBuf): BilateralContent {
        super.convert(buf)
        bool = buf.readBoolean()
        return this
    }

    override fun toString(): String {
        return "BooleanContent{bool=$bool}"
    }

    override fun getData(): Any = bool!!

    override fun write(buf: PacketByteBuf): PacketByteBuf {
        super.write(buf)
        buf.writeBoolean(bool!!)
        return buf
    }

    override fun setData(data: Any) { bool = data as Boolean }
}

