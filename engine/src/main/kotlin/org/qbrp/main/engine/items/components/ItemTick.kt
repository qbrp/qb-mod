package org.qbrp.main.engine.items.components

import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.game.loop.Tick
import org.qbrp.main.core.mc.player.ServerPlayerObject
import org.qbrp.main.core.mc.player.PlayersUtil

interface ItemTick : Tick<ItemTickContext> {
    override fun tick(context: ItemTickContext) {
        val placeholders = context.item.placeholders
        context.item.state.getComponentsIsInstance<ItemBehaviour>().forEach {
            placeholders.putAll(it.updatePlaceholders(placeholders))
        }
        val itemStack = context.itemStack ?: return
        val player = context.player
        val entity = context.entity

        when {
            player != null -> {
                inventoryTick(itemStack)
                playerInventoryTick(player, itemStack)
            }
            entity is ItemEntity -> itemEntityTick(entity, entity.stack)
            else -> inventoryTick(itemStack)
        }

        detectViewerAndTick(itemStack, entity)
    }

    private fun detectViewerAndTick(itemStack: ItemStack, entity: Entity?) {
        detectViewer(itemStack, entity)?.let { viewer ->
            visibleTick(viewer, itemStack)
        }
    }

    private fun detectViewer(stack: ItemStack, entity: Entity?): ServerPlayerObject? {
        val player = entity as? ServerPlayerEntity ?: return null
        val screenHandler = player.currentScreenHandler ?: return null

        val isVisible = screenHandler.slots.any { it.stack === stack }
        return if (isVisible) PlayersUtil.getPlayerSessionOrNull(player) else null
    }

    fun playerInventoryTick(playerObject: ServerPlayerObject, itemStack: ItemStack) = Unit
    fun inventoryTick(itemStack: ItemStack) = Unit
    fun itemEntityTick(itemEntity: ItemEntity, itemStack: ItemStack) = Unit
    fun visibleTick(playerObject: ServerPlayerObject, itemStack: ItemStack) = Unit
}
