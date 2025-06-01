package org.qbrp.main.core.utils.networking.messages.types
import net.minecraft.network.PacketByteBuf

class IntContent(var int: Int? = 0) : BilateralContent() {

    override fun toString(): String = "$int"
    override fun getData(): Int = int!!
    override fun setData(data: Any) { int = data as Int}
    fun setData(data: Double) { int = data.toInt() }

    override fun convert(buf: PacketByteBuf): IntContent { int = buf.readInt(); return this }
    override fun write(buf: PacketByteBuf): PacketByteBuf {
        return buf.apply { writeInt(int ?: 0) }
    }
}