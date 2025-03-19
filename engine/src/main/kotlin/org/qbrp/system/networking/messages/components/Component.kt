package org.qbrp.system.networking.messages.components
import net.minecraft.network.PacketByteBuf
import org.qbrp.system.networking.messages.types.BilateralContent
import org.qbrp.system.networking.messages.types.SendContent
import org.qbrp.system.networking.messages.types.Signal
import java.util.UUID

open class Component(val name: String = "", var content: SendContent = Signal(), val meta: Map<String, String> = emptyMap()): SendContent {
    override var messageId: String = UUID.randomUUID().toString()

    override fun toString(): String {
        return "Component(name='$name', content=$content, meta=$meta)"
    }

    companion object {
        private val factory = ComponentTypeFactory()
    }

    fun copy(): Component {
        return Component(
            name = this.name,
            content = this.content, // Глубокая копия данных
            meta = this.meta.toMap() // Глубокая копия метаданных
        )
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

    override fun setData(data: Any) {
        content = data as SendContent
    }
}
