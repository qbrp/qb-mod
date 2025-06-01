package org.qbrp.main.core.utils.networking.messages.types

import net.minecraft.network.PacketByteBuf

class Signal: BilateralContent() {
    override fun write(buf: PacketByteBuf): PacketByteBuf { return super.write(buf); }
    override fun setData(data: Any) = throw UnsupportedOperationException()
    override fun convert(buf: PacketByteBuf): Signal { return super.convert(buf) as Signal; }
    override fun getData(): Any = throw UnsupportedOperationException()
}