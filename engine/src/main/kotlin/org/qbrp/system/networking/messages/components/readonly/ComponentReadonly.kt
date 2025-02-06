package org.qbrp.system.networking.messages.components.readonly

import net.minecraft.network.PacketByteBuf
import org.qbrp.system.networking.messages.components.Component
import org.qbrp.system.networking.messages.components.ComponentTypeFactory
import org.qbrp.system.networking.messages.types.BilateralContent
import org.qbrp.system.networking.messages.types.ReceiveContent
import java.util.UUID

class ComponentReadonly(private val component: Component?): ReceiveContent {
    override var messageId: String = UUID.randomUUID().toString()

    companion object {
        private val factory = ComponentTypeFactory()
    }

    override fun getData(): Component {
        return component!!
    }

    override fun convert(buf: PacketByteBuf): ComponentReadonly {
        val name = buf.readString()
        val type = buf.readString()
        var meta = emptyMap<String, String>()
        if (buf.readBoolean() == true) {
            meta = buf.readMap(::HashMap, PacketByteBuf::readString, PacketByteBuf::readString)
        }
        val typeObject = factory.buildComponentType(type)
        val content = (typeObject as BilateralContent).convert(buf)
        return ComponentReadonly(Component(name, content, meta))
    }
}