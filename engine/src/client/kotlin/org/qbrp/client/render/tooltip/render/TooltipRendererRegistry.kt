package org.qbrp.client.render.tooltip.render

import org.qbrp.client.render.dataformats.Vec2
import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.main.mechanics.tooltip.component.TooltipComponent
import org.qbrp.client.render.ui.components.layout.Size
import org.qbrp.client.render.ui.components.tooltip.Tooltip
import kotlin.reflect.KClass

class TooltipRendererRegistry: MainTooltipRenderer {
    private val associations: MutableMap<KClass<out TooltipComponent>, TooltipComponentRenderer<*>> = mutableMapOf()

    private fun getRenderer(data: TooltipComponent) =
        (associations[data::class] as? TooltipComponentRenderer<TooltipComponent>)
            ?: throw RuntimeException("Renderer for ${data::class.simpleName} not regisreted")

    fun <K: TooltipComponent> register(data: KClass<K>, renderer: TooltipComponentRenderer<K>) {
        associations[data] = renderer
    }

    override fun measure(
        data: TooltipComponent,
    ): Size {
        return getRenderer(data).measure(data)
    }

    override fun draw(
        data: TooltipComponent,
        pos: Vec2,
        context: Draw2dContext,
        tooltip: Tooltip
    ) {
        context.matrices.translate(pos.x.toDouble(), pos.y.toDouble(), 0.0)
        getRenderer(data).draw(data, pos, context, tooltip)
        context.matrices.peek()
    }
}