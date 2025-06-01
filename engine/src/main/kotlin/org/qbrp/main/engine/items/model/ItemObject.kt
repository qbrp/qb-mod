package org.qbrp.main.engine.items.model


import net.minecraft.item.ItemStack
import net.minecraft.util.math.Vec3d
import org.qbrp.main.core.game.lifecycle.Lifecycle
import org.qbrp.main.core.game.loop.Tick
import org.qbrp.main.core.mc.McObject
import org.qbrp.main.core.mc.player.PlayerObject

class ItemObject(lifecycle: ItemLifecycle,
                 var owner: PlayerObject? = null,
                 val type: String = "abstract_generated"):
    McObject(lifecycle as Lifecycle<McObject>), Tick<Unit> {
    var itemStack: ItemStack? = null

    override fun tick(context: Unit) = tickState(context)

    private val module = lifecycle.module

    fun copyItemStack(): ItemStack = module.copyItemStack(this)

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