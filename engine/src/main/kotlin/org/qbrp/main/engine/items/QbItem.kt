package org.qbrp.main.engine.items

import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import org.koin.core.component.KoinComponent
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.items.model.ItemStorage
import org.qbrp.main.engine.items.model.ServerItemObject

class QbItem(): Item(ItemsModule.SETTINGS), KoinComponent {
    companion object {
        val items by lazy { Engine.getModule<ItemsModule>() }
        val storage by lazy { items.getLocal<ItemStorage<ServerItemObject>>() }
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand?): TypedActionResult<ItemStack?>? {
        val stack = user.handItems.first()
        storage.getItemObject(stack)?.handleUse(stack, world, user)
        return super.use(world, user, hand)
    }
}