package org.qbrp.main.engine.items.model

import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.components.objects.StateObject
import org.qbrp.main.core.utils.log.Logger
import org.qbrp.main.core.utils.log.LoggerUtil
import org.qbrp.main.engine.containers.v2.entity.ContainerEntity
import org.qbrp.main.engine.containers.v2.entity.ContainerEntry
import org.qbrp.main.engine.items.components.ItemTick
import org.qbrp.main.engine.items.components.StateTick
import org.qbrp.main.engine.items.components.ExactTick
import org.qbrp.main.engine.items.components.meta.Meta
import org.qbrp.main.mechanics.items.display.components.ItemDisplay

open class ItemObject(state: ItemState) : StateObject<ItemState>(state), ContainerEntity, ItemTick, ContainerEntry {
    override fun tickState() {
        filterIsInstance<StateTick>().forEach { it.tickState() }
    }

    override fun tickStateExact() {
        filterIsInstance<ExactTick>().forEach { it.tickStateExact() }
    }

    companion object {
        protected val TICK_LOGGER = LoggerUtil.register(Logger(enabled = false, "item", "tick"))
        const val ITEM_TYPE = "abstract_item"
    }
}
