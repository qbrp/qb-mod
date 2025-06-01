package org.qbrp.main.core.utils.networking.messages.types
import net.minecraft.network.PacketByteBuf

class DoubleContent(var double: Double? = 0.0) : BilateralContent() {

    override fun toString(): String = "$double"
    override fun getData(): Double = double!!
    override fun setData(data: Any) { double = data as Double }

    override fun convert(buf: PacketByteBuf): DoubleContent { double = buf.readDouble(); return this }
    override fun write(buf: PacketByteBuf): PacketByteBuf {
        return buf.apply { writeDouble(double ?: 0.0) }
    }
}