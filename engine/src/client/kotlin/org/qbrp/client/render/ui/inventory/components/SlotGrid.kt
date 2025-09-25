package org.qbrp.client.render.ui.inventory.components

import org.qbrp.client.render.Drawable
import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.client.render.ui.components.behaviour.Highlight
import org.qbrp.client.render.ui.components.behaviour.HighlightParams
import org.qbrp.client.render.ui.components.behaviour.OnHover
import org.qbrp.client.render.ui.components.layout.Constraints
import org.qbrp.client.render.ui.components.layout.LayoutElement
import org.qbrp.client.render.ui.components.layout.Size
import org.qbrp.client.render.ui.components.transform.RectTransform
import org.qbrp.client.render.ui.dsl.viewOf
import org.qbrp.client.render.ui.interaction.event.ItemSelection
import org.qbrp.client.render.ui.view.HitBehavior
import org.qbrp.client.render.ui.view.View
import org.qbrp.client.render.ui.view.ViewComponent
import org.qbrp.client.render.ui.view.ViewContext
import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.core.components.extensions.require
import org.qbrp.main.engine.items.model.ItemId
import kotlin.math.min

class SlotGrid(
    ctx: ViewContext,
    private val slotItems: SlotItems,
    private val slotHoverListener: SlotHoverListener? = null,
    private val slotClickListener: SlotClickListener? = null,
    var maxColumns: Int? = null,
    var maxRows: Int? = null
) : ViewComponent(ctx), LayoutElement, ItemSelection, Drawable {
    private var items: List<SlotItem> = emptyList()
    private val slots: MutableList<Slot> = mutableListOf()
    override val selectedItem: ItemId?
        get() {
            return slotItems.provideItems().getOrNull((hoveredSlot?.index ?: return null))?.id
        }
    val hoveredSlot get() = slots.find { it.isHover }

    var columns: Int = 0
        private set
    var rows: Int = 0
        private set

    override fun measure(c: Constraints): Size {
        return Size(columns * 18f, rows * 18f)
    }

    override fun onRender(ctx: Draw2dContext) {
        updateItems()
    }

    private fun updateItems() {
        val newItems = slotItems.provideItems().toMutableList()
        if (newItems.size != items.size) {
            items = newItems
            computeGrid()
            buildSlots()
            view.require<RectTransform>().invalidateMeasure()
        }
    }

    private fun buildSlots() {
        clearSlots()
        items.forEachIndexed { index, item ->
            val col = index % columns
            val row = index / columns
            val x = (col * 17).toFloat()
            val y = (row * 18).toFloat()
            addSlotView(item, index, x, y)
        }
    }

    private fun addSlotView(item: SlotItem, index: Int, x: Float, y: Float) {
        view.addView(
            View().apply {
                setPos(x, y)
                hitBehavior = HitBehavior.TRANSLUCENT
                width = 18f
                height = 18f
                add(OnHover(componentContext, Highlight(HighlightParams(padding = 2f))))
                add(Slot(componentContext, index, item).apply {
                    slots.add(this)
                    clickListener = slotClickListener
                    hoverListener = slotHoverListener
                })
            }
        )
    }

    private fun computeGrid() {
        if (items.isEmpty()) {
            rows = 0
            columns = 0
            return
        }
        this.columns = maxColumns?.let { min(items.size, it) } ?: items.size
        val rows = (items.size + columns - 1) / columns
        this.rows = maxRows?.let { min(rows, it) } ?: rows
    }

    fun clearSlots() {
        slots.clear()
        view.removeAllViews()
    }
}