package org.qbrp.main.core.utils.networking.messages.types

import net.minecraft.network.PacketByteBuf

open class GenericContent(val writeLambda: (PacketByteBuf) -> PacketByteBuf,
                          val convertLambda: (PacketByteBuf) -> BilateralContent ): BilateralContent() {

    override fun write(buf: PacketByteBuf): PacketByteBuf { super.write(buf); return writeLambda(buf) }
    override fun setData(data: Any) { UnsupportedOperationException() }
    override fun convert(buf: PacketByteBuf): BilateralContent { super.convert(buf); return convertLambda(buf) }
    override fun getData(): Any = UnsupportedOperationException()
}