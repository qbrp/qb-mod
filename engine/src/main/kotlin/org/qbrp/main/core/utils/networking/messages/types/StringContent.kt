package org.qbrp.main.core.utils.networking.messages.types
import net.minecraft.network.PacketByteBuf

class StringContent(var string: String? = "") : BilateralContent() {

    override fun toString(): String = string!!
    override fun getData(): String = string!!
    override fun setData(data: Any) { string = data.toString() }

    override fun convert(buf: PacketByteBuf): StringContent { string = buf.readString(); return this }
    override fun write(buf: PacketByteBuf): PacketByteBuf { return buf.writeString(string) }
}