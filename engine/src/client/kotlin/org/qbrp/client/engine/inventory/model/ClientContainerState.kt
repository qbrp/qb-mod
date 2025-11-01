package org.qbrp.client.engine.inventory.model

import org.qbrp.client.render.item.model.data.ItemDisplay
import org.qbrp.main.engine.tooltip.component.TooltipComponent
import org.qbrp.client.render.ui.inventory.components.SlotItem
import org.qbrp.client.render.ui.inventory.components.SlotItems
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.engine.containers.components.ContainerDisplayName
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.containers.entity.ContainerEntry
import org.qbrp.main.engine.containers.entity.OccupancyView
import org.qbrp.main.engine.items.model.ItemId

data class ClientContainerState(
    val id: ContainerId,
    var name: ContainerDisplayName,
    val items: MutableList<ContainerEntry>,
    var occupancy: OccupancyView,
    var tooltip: List<TooltipComponent> = emptyList()
) : SlotItems {
    fun updateItems(list: List<ContainerEntry>) {
        items.clear()
        items.addAll(list)
    }

    override fun provideItems(): List<SlotItem> {
        return items.mapNotNull { item ->
            val display = item.get<ItemDisplay>() ?: return@mapNotNull null
            SlotItem(ItemId(item.id), display)
        }
    }
}