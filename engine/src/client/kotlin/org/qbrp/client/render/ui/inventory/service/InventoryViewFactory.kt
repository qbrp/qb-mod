package org.qbrp.client.render.ui.inventory.service

import org.qbrp.client.engine.inventory.model.ClientContainerState
import org.qbrp.client.engine.inventory.model.InventoryDefinition
import org.qbrp.client.engine.inventory.service.InventoryCloseListener
import org.qbrp.client.render.Render
import org.qbrp.main.mechanics.tooltip.component.PlainTextTooltipComponent
import org.qbrp.main.mechanics.tooltip.component.ProgressBarTooltipComponent
import org.qbrp.main.mechanics.tooltip.component.TooltipComponent
import org.qbrp.main.mechanics.tooltip.TooltipViewData
import org.qbrp.client.render.ui.UiContext
import org.qbrp.client.render.ui.components.behaviour.ShowTooltip
import org.qbrp.client.render.ui.components.tooltip.TooltipManager
import org.qbrp.client.render.ui.inventory.components.SlotGrid
import org.qbrp.client.render.ui.dsl.widget
import org.qbrp.client.render.ui.inventory.components.SlotClickListener
import org.qbrp.client.render.ui.inventory.components.SlotHoverListener
import org.qbrp.client.render.ui.inventory.entity.InventoryWidget
import org.qbrp.client.render.ui.inventory.entity.InventoryWidgetGroup
import org.qbrp.client.render.ui.inventory.entity.anchorsPos
import org.qbrp.client.render.ui.inventory.entity.pivot
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.containers.v2.entity.OccupancyView
import org.qbrp.main.engine.inventory.entity.ScreenCorner
import org.qbrp.main.engine.text.literal
import kotlin.collections.plus

class InventoryViewFactory(
    private val defaultSlotHoverListener: SlotHoverListener,
    private val defaultSlotClickListener: SlotClickListener,
    private val inventoryCloseListener: InventoryCloseListener,
    private val tooltipManager: TooltipManager
) {
    context(ctx: UiContext)
    fun new(definitions: List<InventoryDefinition>, position: ScreenCorner): InventoryWidgetGroup {
        require(definitions.isNotEmpty())
        val widgets = definitions.map { InventoryWidget(buildView(it.container.id, it, position), it.params, it.container) }

        return InventoryWidgetGroup(widgets)
    }

    context(ctx: UiContext)
    private fun buildView(id: ContainerId, definition: InventoryDefinition, position: ScreenCorner) = widget(id.raw) {
        anchors(position.anchorsPos.x, position.anchorsPos.y, position.pivot)
        title(definition.container.name.value)

        root {
            onClose { view -> inventoryCloseListener.onInventoryClosed(view, id) }
        }

        actionbar {
            button {
                texture(Render.INFO, 16f)
                hover {
                    listen(ShowTooltip(InventoryTooltip(definition.container), tooltipManager, ctx))
                }
            }
        }

        content {
            hover { translucent() }
            component { ctx ->
                val params = definition.params
                SlotGrid(
                    ctx,
                    definition.container,
                    slotHoverListener = defaultSlotHoverListener,
                    slotClickListener = defaultSlotClickListener,
                    maxColumns = params.maxColumns,
                    maxRows = params.maxRows
                )
            }
        }
    }

    data class InventoryTooltip(val containerState: ClientContainerState) : TooltipViewData {
        private var cachedKey: Key? = null
        private var cachedComponents: List<TooltipComponent> = emptyList()

        override val components: List<TooltipComponent>
            get() {
                val occ = containerState.occupancy
                val base = containerState.tooltip
                val key = Key(occ, base)

                if (key != cachedKey) {
                    val occupancyTooltip = when (occ) {
                        is OccupancyView.Limited -> ProgressBarTooltipComponent(
                            occ.fullness.toFloat(),
                            50f,
                            ProgressBarTooltipComponent.ColorProfile.GREEN_RED,
                            literal("Заполненность")
                        )
                        is OccupancyView.Unlimited -> PlainTextTooltipComponent.of("Вместимость не ограничена.")
                    }
                    cachedComponents = base + occupancyTooltip
                    cachedKey = key
                }
                return cachedComponents
            }

        private data class Key(
            val occupancy: OccupancyView,
            val tooltipRef: Any?
        )
    }
}