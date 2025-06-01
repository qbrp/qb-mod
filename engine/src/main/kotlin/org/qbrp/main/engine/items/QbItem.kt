package org.qbrp.main.engine.items

import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.item.Item.Settings
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.world.World
import org.koin.core.component.KoinComponent
import org.qbrp.main.core.mc.player.PlayersModule
import org.qbrp.main.core.mc.player.PlayersUtil
import org.qbrp.main.engine.items.model.ItemObject

class QbItem(): net.minecraft.item.Item(Settings()
    .maxCount(1)
), KoinComponent {
    //val gameAPI = Engine.getAPI<GameAPI>()

    fun getObject(stack: ItemStack): ItemObject? {
        stack.nbt?.getLong("id").let { id ->
            //return gameAPI?.getOrLoad<ItemObject, ItemLifecycle>(id!!)
            return null
        }
    }

    override fun inventoryTick(stack: ItemStack, world: World, entity: Entity, slot: Int, selected: Boolean) {
        if (!world.isClient) {
            getObject(stack)?.let { item ->
                if (entity !is ServerPlayerEntity) return
                val plr = PlayersUtil.getPlayerSession(entity.name.string)
                if (plr == null) return
                item.owner = plr
                item.itemStack = stack
            }
        }
    }

    override fun onItemEntityDestroyed(entity: ItemEntity) {
        super.onItemEntityDestroyed(entity)
        getObject(entity.stack)?.destroy()
    }
}