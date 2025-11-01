package org.qbrp.client.render.tooltip.components

import org.qbrp.client.render.dataformats.Vec2
import org.qbrp.client.render.dataformats.vec2
import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.client.render.text.render.model.TextRenderer
import org.qbrp.client.render.tooltip.render.TooltipComponentRenderer
import org.qbrp.client.render.ui.components.layout.Size
import org.qbrp.client.render.ui.components.tooltip.Tooltip
import org.qbrp.main.engine.Colors
import org.qbrp.main.engine.tooltip.component.ProgressBarTooltipComponent

class ProgressBarTooltipRenderer(private val textRenderer: TextRenderer): TooltipComponentRenderer<ProgressBarTooltipComponent> {
    private val annotationPos = vec2(0, 0)
    override fun measure(data: ProgressBarTooltipComponent): Size {
        val width = textRenderer.getWidth(data.annotation) + data.progressBarWidth
        val height = textRenderer.getHeight(data.annotation)
        return Size(width, height)
    }

    override fun draw(
        data: ProgressBarTooltipComponent,
        pos: Vec2,
        context: Draw2dContext,
        tooltip: Tooltip
    ) {
        textRenderer.render(
            context.matrices,
            context.vertexConsumers,
            data.annotation,
            annotationPos
        )
        val annotationWidth = textRenderer.getWidth(data.annotation)
        val annotationHeight = textRenderer.getHeight(data.annotation)
        val gap = 2f
        val progressBarStartX = annotationWidth + gap
        val progressBarEndX = progressBarStartX + data.progressBarWidth
        val progressEndX = progressBarStartX + (data.progress * data.progressBarWidth)

        val color = data.colorProfile.let { Colors.lerp(it.startColor, it.endColor, data.progress) }

        context.fill(progressBarStartX, 1f, progressBarEndX, annotationHeight - 1, Colors.GRAY)
        context.fill(
            progressBarStartX, 1f,
            progressEndX, annotationHeight - 1,
            color
        )
    }
}