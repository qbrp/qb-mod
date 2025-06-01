package org.qbrp.main.core.utils.networking.messages.types

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.network.PacketByteBuf
import java.util.UUID

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

