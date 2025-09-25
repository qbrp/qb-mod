package org.qbrp.client.render.tooltip.components

import org.qbrp.client.render.dataformats.Vec2
import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.client.render.text.render.model.TextRenderer
import org.qbrp.client.render.tooltip.render.TooltipComponentRenderer
import org.qbrp.client.render.ui.components.layout.Size
import org.qbrp.client.render.ui.components.tooltip.Tooltip
import org.qbrp.client.render.ui.components.transform.RectTransform
import org.qbrp.main.core.components.extensions.require
import org.qbrp.main.mechanics.tooltip.component.NameTooltipComponent

class NameTooltipRenderer(val textRenderer: TextRenderer, val padding: Float, val lineHeight: Float): TooltipComponentRenderer<NameTooltipComponent> {
    override fun measure(data: NameTooltipComponent): Size {
        val textH = textRenderer.getHeight(data.name)
        val h = textH + lineHeight + (padding * 2)
        val w = textRenderer.getWidth(data.name)
        return Size(w, h)
    }

    override fun draw(
        data: NameTooltipComponent,
        pos: Vec2,
        context: Draw2dContext,
        tooltip: Tooltip
    ) {
        textRenderer.render(context.matrices, context.vertexConsumers, data.name, pos)
        val rect = tooltip.view.require<RectTransform>()
        val h = textRenderer.getHeight(data.name)
        val colorProfile = tooltip.color
        context.fillGradientVertical(
            0f,
            h + padding,
            rect.measured.w,
            h + padding + lineHeight,
            0.01f,
            colorProfile.borderStartY,
            colorProfile.borderEndY
        )
    }
}