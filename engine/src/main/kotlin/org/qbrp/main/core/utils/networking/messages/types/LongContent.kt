package org.qbrp.main.core.utils.networking.messages.types
import net.minecraft.network.PacketByteBuf

class LongContent(var long: Long? = 0) : BilateralContent() {

    override fun toString(): String = "$long"
    override fun getData(): Long = long!!
    override fun setData(data: Any) { long = data as Long}
    fun setData(data: Long) { long = data.toLong() }

    override fun convert(buf: PacketByteBuf): LongContent { long = buf.readLong(); return this }
    override fun write(buf: PacketByteBuf): PacketByteBuf {
        return buf.apply { writeLong(long ?: 0) }
    }
}