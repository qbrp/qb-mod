package org.qbrp.main.engine.items.components

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import org.qbrp.main.core.components.Component
import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.engine.items.model.ItemObject
import org.qbrp.main.engine.items.model.ServerItemObject
import org.qbrp.main.engine.items.model.ItemState

abstract class ItemComponent(ctx: ItemComponentInstantiateContext): ItemComponentBehaviour, ComponentManager by ctx.componentManager {
    override var state: ItemState = ctx.componentManager
}