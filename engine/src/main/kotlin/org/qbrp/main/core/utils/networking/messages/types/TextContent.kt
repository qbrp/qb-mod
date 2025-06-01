package org.qbrp.main.core.utils.networking.messages.types
import net.minecraft.network.PacketByteBuf
import net.minecraft.text.Text

class TextContent(var text: Text? = Text.literal("")) : BilateralContent() {

    override fun toString(): String = text.toString()
    override fun getData(): Text = text!!
    override fun setData(data: Any) { text = data as Text? }

    override fun convert(buf: PacketByteBuf): TextContent { text = buf.readText(); return this }
    override fun write(buf: PacketByteBuf): PacketByteBuf { return buf.writeText(text) }
}