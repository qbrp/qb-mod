package org.qbrp.main.engine.items


import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.qbrp.main.core.game.model.objects.BaseObject
import org.qbrp.main.core.mc.registry.items.ItemRegistry
import org.qbrp.main.core.utils.log.Logger
import org.qbrp.main.core.utils.log.LoggerUtil
import org.qbrp.main.engine.items.components.ItemTickContext
import org.qbrp.main.engine.inventory.Stackable
import org.qbrp.main.engine.items.components.model.ItemModel
import org.qbrp.main.engine.items.model.PropTickHandler

open class ItemObject(
    val type: String = "abstract_item",
    override val placeholders: MutableMap<String, String> = mutableMapOf(),
) : BaseObject(), PropTickHandler, ItemTickContext, PlaceholdersContainer, Stackable, KoinComponent {

    override val item = this
    override var itemStack: ItemStack? = null
    override var entity: Entity? = null

    override fun tick(itemStack: ItemStack, entity: Entity?) {
        this.itemStack = itemStack
        this.entity = entity
        TICK_LOGGER.log("Tick $id. Entity: ${entity?.name?.string} (${entity?.javaClass?.simpleName}). Holder: ${holder?.name?.string}")
        tickState(this)
    }

    fun copyItemStack(): ItemStack {
        val definition = get<ItemRegistry>().getItem(item.type).get()
        return ItemStack(definition).apply {
            orCreateNbt.putString("id", id)
            orCreateNbt.putString("QbrpModel", getComponent<ItemModel>()?.model)
        }
    }

    override fun asItemStack(): ItemStack = itemStack ?: copyItemStack()

    companion object {
        protected val TICK_LOGGER = LoggerUtil.register(Logger(enabled = false, "item", "tick"))
    }
}
