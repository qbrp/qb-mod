package org.qbrp.engine.items.model

import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.core.game.Game
import org.qbrp.core.resources.content.ItemConfig

class ItemManager() {
    fun wrapItem(config: ItemConfig, tag: String, player: ServerPlayerEntity): ItemStack {
        val itemTag = Game.items.getBaseItem(config.parent)?.get()
        val stack = ItemStack(itemTag).apply {
            (item as QbItem).createBlankData(config.getTag(tag), config)
        }
        player.giveItemStack(stack)
        return stack
    }
}