package org.qbrp.main.mechanics.items.display

import net.minecraft.network.PacketByteBuf
import org.qbrp.main.core.networking.readProtobuf
import org.qbrp.main.core.networking.readStringList
import org.qbrp.main.core.networking.readTextComponent
import org.qbrp.main.core.networking.writeStringList
import org.qbrp.main.core.networking.writeTextComponent
import org.qbrp.main.engine.items.ModelId
import org.qbrp.main.engine.items.model.ItemId
import org.qbrp.main.engine.items.networking.ItemSyncPacket
import org.qbrp.main.engine.text.components.TextComponent
import org.qbrp.main.mechanics.ItemModelId

class ItemDisplayPacket(itemId: String, val model: ModelId, val name: TextComponent, val description: List<TextComponent>): ItemSyncPacket(itemId) {
    constructor(buf: PacketByteBuf) : this(
        buf.readString(),
        ItemModelId(buf.readString()),
        buf.readTextComponent(),
        buf.readList<TextComponent> { buf -> buf.readTextComponent() }
    )

    override fun write(buf: PacketByteBuf) {
        super.write(buf)
        buf.writeString(model.modelId)
        buf.writeTextComponent(name)
        buf.writeCollection(description) { buf, component -> buf.writeTextComponent(component) }
    }
}