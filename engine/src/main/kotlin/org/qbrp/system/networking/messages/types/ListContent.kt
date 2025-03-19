package org.qbrp.system.networking.messages.types

import net.minecraft.network.PacketByteBuf
import kotlin.collections.List

open class ListContent<T>(val writer: (PacketByteBuf, T) -> Unit, val reader: (PacketByteBuf) -> T) : BilateralContent() {
    var list: List<T> = emptyList()
    override fun write(buf: PacketByteBuf): PacketByteBuf {
        super.write(buf);
        buf.writeCollection(list, writer)
        return buf
    }

    override fun setData(data: Any) {
        list = data as List<T>
    }

    override fun convert(buf: PacketByteBuf): BilateralContent {
        super.convert(buf);
        list = buf.readCollection({ size -> ArrayList<T>(size) }, reader)
        return this
    }

    override fun getData(): List<T> = list
}