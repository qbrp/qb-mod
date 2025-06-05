package org.qbrp.main.engine.items


import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import org.koin.core.component.KoinComponent
import org.qbrp.main.core.game.model.objects.BaseObject
import org.qbrp.main.core.utils.log.Logger
import org.qbrp.main.core.utils.log.LoggerUtil
import org.qbrp.main.engine.items.components.ItemTickContext

open class ItemObject(
    val type: String = "abstract_item",
    override val placeholders: MutableMap<String, String> = mutableMapOf(),
) : BaseObject(), ItemTickContext, KoinComponent, PlaceholdersContainer {

    override val item = this
    override var itemStack: ItemStack? = null
    override var entity: Entity? = null

    fun tick(itemStack: ItemStack, entity: Entity?) {
        this.itemStack = itemStack
        this.entity = entity
        TICK_LOGGER.log("Tick $id. Entity: ${entity?.name?.string} (${entity?.javaClass?.simpleName}). Holder: ${holder?.name?.string}")
        tickState(this)
    }

    companion object {
        protected val TICK_LOGGER = LoggerUtil.register(Logger(enabled = false, "item", "tick"))
    }
}
