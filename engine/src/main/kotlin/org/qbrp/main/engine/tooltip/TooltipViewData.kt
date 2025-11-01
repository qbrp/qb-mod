package org.qbrp.main.engine.tooltip

import org.qbrp.main.engine.tooltip.component.TooltipComponent

interface TooltipViewData {
    val components: List<TooltipComponent>
    val colorProfile: TooltipColorProfile get() = TooltipColorProfile.GRAY
}