package org.qbrp.main.mechanics.tooltip

import org.qbrp.main.mechanics.tooltip.component.TooltipComponent

interface TooltipViewData {
    val components: List<TooltipComponent>
    val colorProfile: TooltipColorProfile get() = TooltipColorProfile.GRAY
}