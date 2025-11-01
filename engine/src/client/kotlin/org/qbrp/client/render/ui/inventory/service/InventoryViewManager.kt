package org.qbrp.client.render.ui.inventory.service

import org.qbrp.client.engine.inventory.model.ClientContainerState
import org.qbrp.client.engine.inventory.model.InventoryDefinition
import org.qbrp.client.render.ui.UiContext
import org.qbrp.client.render.ui.interaction.event.InventorySelection
import org.qbrp.client.render.ui.inventory.entity.InventoryWidget
import org.qbrp.client.render.ui.inventory.entity.InventoryWidgetGroup
import org.qbrp.client.render.ui.view.ViewManager
import org.qbrp.client.render.ui.view.isHovered
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.network.inventory.ScreenCorner
import java.util.concurrent.CopyOnWriteArrayList

class InventoryViewManager(
    private val viewManager: ViewManager,
    private val factory: InventoryViewFactory,
    private val uiContext: UiContext
) : InventorySelection {
    val openInventories = CopyOnWriteArrayList<InventoryWidget>()
    override val selectedInventory: ContainerId? get() = with(uiContext) { computeSelectedInventory()?.container?.id }

    context(ctx: UiContext)
    fun onOpen(definitions: List<InventoryDefinition>, position: ScreenCorner) {
        val inventory = factory.new(definitions, position)
        inventory.widgets.forEach {
            viewManager.openView(it.view, it.container.id.raw)
            openInventories.add(it)
        }
    }

    fun closeAll() {
        openInventories.forEach {
            openInventories.remove(it)
            viewManager.closeView(it.view)
        }
    }

    fun onClose(data: ClientContainerState) {
        openInventories.find { it.container.id == data.id }?.let {
            openInventories.remove(it)
            viewManager.closeView(it.view)
        }
    }

    context(ctx: UiContext)
    private fun computeSelectedInventory(): InventoryWidget? {
        for (inventory in openInventories) {
            if (inventory.view.isHovered()) return inventory
        }
        return null
    }
}