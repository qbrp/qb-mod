package org.qbrp.system.networking.messages.components
import net.minecraft.network.PacketByteBuf
import org.qbrp.system.networking.messages.types.BilateralContent
import org.qbrp.system.networking.messages.types.SendContent
import org.qbrp.system.networking.messages.types.Signal
import java.util.UUID

data class Component(val name: String = "", val content: SendContent = Signal(), val meta: Map<String, String> = emptyMap()): SendContent {
    override var messageId: String = UUID.randomUUID().toString()

    companion object {
        private val factory = ComponentTypeFactory()
    }

    override fun write(buf: PacketByteBuf): PacketByteBuf {
        buf.writeString(name)
        buf.writeString(factory.getComponentId(content))
        if (meta.isNotEmpty()) {
            buf.writeBoolean(true)
            buf.writeMap(meta,
            { writer, key -> writer.writeString(key) },
            { writer, value -> writer.writeString(value) })
        } else {
            buf.writeBoolean(false)
        }
        content.write(buf)
        return buf
    }
}
