package org.qbrp.client.render.ui.components.tooltip

import org.qbrp.client.core.mouse.MouseState
import org.qbrp.main.engine.tooltip.TooltipColorProfile
import org.qbrp.main.engine.tooltip.TooltipViewData
import org.qbrp.client.render.tooltip.render.MainTooltipRenderer
import org.qbrp.client.render.tooltip.render.TooltipBackgroundRenderer
import org.qbrp.client.render.ui.UiContext
import org.qbrp.client.render.ui.components.layout.Anchors
import org.qbrp.client.render.ui.components.transform.RectTransform
import org.qbrp.client.render.ui.dsl.viewOf
import org.qbrp.client.render.ui.view.View
import org.qbrp.client.render.ui.view.ViewManager
import org.qbrp.main.core.components.extensions.require

class TooltipManager(
    private val viewManager: ViewManager,
    private val tooltipRenderer: MainTooltipRenderer,
    private val tooltipBgRenderer: TooltipBackgroundRenderer,
    private val mouse: MouseState,
) {
    var currentTooltip: View? = null
    private var blocked = false

    fun block() {
        blocked = true
    }

    fun unblock() {
        blocked = false
    }

    context(ui: UiContext)
    fun showTooltip(tooltip: TooltipViewData) {
        if (currentTooltip != null) hideTooltip()
        if (blocked) return
        val view = newTooltipView(tooltip, tooltip.colorProfile)
        viewManager.addView(view)
        currentTooltip = view
    }

    context(ui: UiContext)
    fun showTooltipOrUpdatePos(data: TooltipViewData) {
        val tooltip = currentTooltip
        if (tooltip != null) {
            val rect = tooltip.require<RectTransform>()
            rect.anchors = Anchors(mouse.posNormalized.x, mouse.posNormalized.y)
            rect.invalidateArrange()
        } else {
            showTooltip(data)
        }
    }

    fun hideTooltip() {
        val tooltip = currentTooltip
        if (tooltip == null) return
        viewManager.closeView(tooltip)
        currentTooltip = null
    }

    context(ctx: UiContext)
    private fun newTooltipView(tooltip: TooltipViewData, colorProfile: TooltipColorProfile) = viewOf {
        anchorCursor(mouse)
        margin(l = -14f)
        zIndex(400)
        overlayBehaviour()

        component { Tooltip(it, tooltip, colorProfile, tooltipRenderer) }
        component { TooltipBackground(it, colorProfile, tooltipBgRenderer) }
    }
}