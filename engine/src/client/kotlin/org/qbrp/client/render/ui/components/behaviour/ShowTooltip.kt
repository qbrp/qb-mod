package org.qbrp.client.render.ui.components.behaviour

import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.client.render.ui.UiContext
import org.qbrp.main.engine.tooltip.TooltipViewData
import org.qbrp.client.render.ui.components.tooltip.TooltipManager
import org.qbrp.client.render.ui.view.View
import org.qbrp.client.render.ui.view.ViewComponent

class ShowTooltip(val tooltipData: TooltipViewData, private val tooltipManager: TooltipManager, private val context: UiContext): HoverListener {
    private var showed = false

    override fun onNotHover(ctx: Draw2dContext, view: View) {
        if (showed) {
            tooltipManager.hideTooltip()
            showed = false
        }
    }

    override fun onHover(view: View, component: ViewComponent) = with(context) {
        tooltipManager.showTooltipOrUpdatePos(tooltipData)
        showed = true
    }

    override fun onHoverStart(component: ViewComponent) = with(context) {
        tooltipManager.showTooltipOrUpdatePos(tooltipData)
    }

    override fun onHoverEnd(component: ViewComponent) {
        tooltipManager.hideTooltip()
        showed = false
    }
}