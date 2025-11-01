package org.qbrp.main.engine.items.model

import org.qbrp.main.core.components.objects.StateObject
import org.qbrp.main.core.util.log.Logger
import org.qbrp.main.core.util.log.LoggerUtil
import org.qbrp.main.engine.containers.entity.ContainerEntity
import org.qbrp.main.engine.containers.entity.ContainerEntry
import org.qbrp.main.engine.items.components.ItemTick
import org.qbrp.main.engine.items.components.StateTick
import org.qbrp.main.engine.items.components.ExactTick

open class ItemObject(state: ItemState) : StateObject<ItemState>(state), ContainerEntity, ItemTick, ContainerEntry {
    override fun tickState() {
        filterIsInstance<StateTick>().forEach { it.tickState() }
    }

    override fun tickStateExact() {
        filterIsInstance<ExactTick>().forEach { it.tickStateExact() }
    }
}
