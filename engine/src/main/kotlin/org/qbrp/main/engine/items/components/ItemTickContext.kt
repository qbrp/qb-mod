package org.qbrp.main.engine.items.components

import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.mc.player.PlayerObject
import org.qbrp.main.core.mc.player.PlayersUtil
import org.qbrp.main.engine.items.ItemObject

interface ItemTickContext {
    val item: ItemObject
    var itemStack: ItemStack?
    var entity: Entity?
    val holder: Entity? get() = itemStack?.holder
    val player: PlayerObject? get() {
        return PlayersUtil.getPlayerSessionOrNull(entity as? ServerPlayerEntity ?: return null)
    }
}