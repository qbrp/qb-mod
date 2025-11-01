package org.qbrp.client.engine.items

import net.minecraft.item.ItemStack
import org.qbrp.main.core.components.extensions.require
import org.qbrp.main.engine.items.model.ItemId
import org.qbrp.main.engine.items.model.ItemObject
import org.qbrp.main.engine.items.model.ItemStateImpl

class ClientItemObject(itemId: ItemId): ItemObject(ItemStateImpl()) {
    override val id: String = itemId.value
}