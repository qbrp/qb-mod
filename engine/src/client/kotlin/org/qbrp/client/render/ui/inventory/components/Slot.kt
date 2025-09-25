package org.qbrp.client.render.ui.inventory.components

import org.qbrp.client.render.Drawable
import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.client.render.ui.components.event.Clickable
import org.qbrp.client.render.ui.components.event.Hoverable
import org.qbrp.client.render.ui.view.ViewComponent
import org.qbrp.client.render.ui.view.ViewContext

open class Slot(
    ctx: ViewContext,
    val index: Int,
    var item: SlotItem?,
    var clickListener: SlotClickListener? = null,
    var hoverListener: SlotHoverListener? = null
) : ViewComponent(ctx), Drawable, Hoverable, Clickable {
    var isHover: Boolean = false
    val isEmpty: Boolean get() = item == null

    override fun onRender(ctx: Draw2dContext) {
        val width = view.width
        val height = view.height
        ctx.fill(0f, 0f, width, height, 0xFF555555.toInt())
        ctx.fill(1f, 1f, width - 1, height - 1, 0xFF333333.toInt())

        if (isHover) {
            hoverListener?.onHover(this)
        }

        if (!isEmpty) {
            ctx.drawItem(item!!.display, 1f, 1f)
        }
    }

    override fun onHoverEnter() {
        isHover = true
        hoverListener?.onSlotHoverStart(this)
    }

    override fun onHoverExit() {
        isHover = false
        hoverListener?.onSlotHoverEnd(this)
    }

    override fun onClick(button: Int, lx: Float, ly: Float): Boolean {
        clickListener?.let {
            if (button == 0) it.onSlotClick(this)
            else if (button == 1) it.onSlotRightClick(this)
        }
        return false
    }
}