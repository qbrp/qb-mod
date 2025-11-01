package org.qbrp.client.render.deprecated

import org.qbrp.client.deprecated.TooltipDataAdapter
import org.qbrp.client.render.deprecated.mc.TooltipRenderAdapter
import org.qbrp.main.engine.tooltip.TooltipViewData

interface TooltipAdapterFactory {
    fun newRenderAdapter(tooltip: TooltipDataAdapter): TooltipRenderAdapter
    fun newDataAdapter(tooltip: TooltipViewData): TooltipDataAdapter
}