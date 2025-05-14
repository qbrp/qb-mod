package org.qbrp.engine.items.model

import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.Vec3d
import org.qbrp.core.game.lifecycle.Lifecycle
import org.qbrp.core.mc.Game
import org.qbrp.core.mc.McObject
import org.qbrp.core.mc.player.PlayerObject

class ItemObject(lifecycle: ItemLifecycle,
                 var owner: PlayerObject? = null,
                 val type: String = "abstract_generated"):
    McObject(lifecycle as Lifecycle<McObject>) {
    var itemStack: ItemStack? = null

    fun copyItemStack(): ItemStack {
        return ItemStack(Game.items.getBaseItem(type)!!.get()).apply {
            orCreateNbt.putLong("id", id)
        }
    }

    fun give(playerObject: PlayerObject) {
        playerObject.entity.giveItemStack(copyItemStack())
    }

    override val pos: Vec3d
        get() = owner?.pos ?: Vec3d.ZERO

    override fun getTooltip(): String {
        return ""
    }

    fun destroy() {
        unload()
    }
}