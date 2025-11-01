package org.qbrp.client.render.deprecated.mc

import org.qbrp.client.deprecated.TooltipDataAdapter
import org.qbrp.client.render.deprecated.TooltipAdapterFactory
import org.qbrp.client.render.screen.context.DrawContextAdapterFactory
import org.qbrp.client.render.text.render.model.TextRenderer
import org.qbrp.main.engine.tooltip.TooltipViewData
import org.qbrp.client.render.tooltip.render.MainTooltipRenderer

class TooltipAdapterFactoryImpl(private val textRenderer: TextRenderer,
                                private val draw2dContextFactory: DrawContextAdapterFactory,
                                private val renderer: MainTooltipRenderer
): TooltipAdapterFactory {
    override fun newRenderAdapter(tooltip: TooltipDataAdapter): TooltipRenderAdapter {
        return TooltipRenderAdapter(tooltip, textRenderer, renderer, draw2dContextFactory)
    }

    override fun newDataAdapter(tooltip: TooltipViewData): TooltipDataAdapter {
        return TooltipDataAdapter(tooltip.components)
    }
}