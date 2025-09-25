package org.qbrp.main.engine.items.components

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import org.qbrp.main.core.components.Component
import org.qbrp.main.engine.items.components.ItemComponent
import org.qbrp.main.engine.items.model.ItemObject
import org.qbrp.main.engine.items.model.ItemState
import org.qbrp.main.engine.items.model.ServerItemObject

interface ItemComponentBehaviour: Component<ItemState> {
    val serverItem get() = state.parent as ServerItemObject
    val item get() = this.state.parent
    fun updatePlaceholders(placeholders: Map<String, String>): Map<String, String> { return emptyMap() }
    fun onUse(itemStack: ItemStack, world: World, player: PlayerEntity) {}
}