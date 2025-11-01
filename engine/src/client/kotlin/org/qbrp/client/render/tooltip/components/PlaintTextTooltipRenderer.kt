package org.qbrp.client.render.tooltip.components

import org.qbrp.client.render.dataformats.Vec2
import org.qbrp.client.render.dataformats.vec2
import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.client.render.text.render.model.TextRenderer
import org.qbrp.client.render.text.render.model.TextSplitter
import org.qbrp.client.render.tooltip.render.TooltipComponentRenderer
import org.qbrp.client.render.ui.components.layout.Size
import org.qbrp.client.render.ui.components.tooltip.Tooltip
import org.qbrp.client.render.window.Window
import org.qbrp.main.engine.tooltip.component.PlainTextTooltipComponent

class PlaintTextTooltipRenderer(private val textSplitter: TextSplitter,
                                private val textRenderer: TextRenderer,
                                private val window: Window): TooltipComponentRenderer<PlainTextTooltipComponent> {
    override fun measure(
        data: PlainTextTooltipComponent,
    ): Size {
        val blocks = data.lines.map { textSplitter.splitText(it, (window.widthDp * 0.4f).toFloat()) }
        val lines = blocks.flatMap { it.texts }
        val height = lines.sumOf { (textRenderer.getHeight(it)).toDouble() }
        val width = lines.maxOf { textRenderer.getWidth(it).toDouble() }

        return Size(width, height)
    }

    override fun draw(
        data: PlainTextTooltipComponent,
        pos: Vec2,
        context: Draw2dContext,
        tooltip: Tooltip
    ) {
        val blocks = data.lines.map { textSplitter.splitText(it, (window.widthDp * 0.4f).toFloat()) }
        val lines = blocks.flatMap { it.texts }
        var y = 0f
        lines.forEach { line ->
            val x = 0f
            textRenderer.render(
                context.matrices,
                context.vertexConsumers,
                line,
                vec2(x, y)
            )
            y += textRenderer.getHeight(line)
        }
    }
}