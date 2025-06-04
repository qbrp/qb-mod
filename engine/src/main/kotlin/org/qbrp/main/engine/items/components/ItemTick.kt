package org.qbrp.main.engine.items.components

import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.game.loop.Tick
import org.qbrp.main.core.mc.player.PlayerObject
import org.qbrp.main.core.mc.player.PlayersUtil

interface ItemTick : Tick<ItemTickContext> {
    override fun tick(context: ItemTickContext) {
        val itemStack = context.itemStack ?: return
        val player = context.player
        val entity = context.entity

        when {
            player != null -> playerInventoryTick(player, itemStack)
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

    private fun detectViewer(stack: ItemStack, entity: Entity?): PlayerObject? {
        val player = entity as? ServerPlayerEntity ?: return null
        val screenHandler = player.currentScreenHandler ?: return null

        val isVisible = screenHandler.slots.any { it.stack === stack }
        return if (isVisible) PlayersUtil.getPlayerSessionOrNull(player) else null
    }

    fun playerInventoryTick(playerObject: PlayerObject, itemStack: ItemStack) = Unit
    fun inventoryTick(itemStack: ItemStack) = Unit
    fun itemEntityTick(itemEntity: ItemEntity, itemStack: ItemStack) = Unit
    fun visibleTick(playerObject: PlayerObject, itemStack: ItemStack)
}
