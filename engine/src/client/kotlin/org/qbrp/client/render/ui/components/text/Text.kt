package org.qbrp.client.render.ui.components.text

import org.qbrp.client.render.Drawable
import org.qbrp.client.render.dataformats.vec2
import org.qbrp.client.render.screen.DefaultZIndexes
import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.client.render.text.render.model.TextRenderer
import org.qbrp.client.render.ui.components.layout.Constraints
import org.qbrp.client.render.ui.components.layout.LayoutElement
import org.qbrp.client.render.ui.components.layout.Size
import org.qbrp.client.render.ui.view.ViewComponent
import org.qbrp.client.render.ui.view.ViewContext
import org.qbrp.main.engine.text.components.TextComponent
import org.qbrp.main.engine.text.components.LiteralTextComponent

typealias TextSupplier = () -> TextComponent

class Text(
    ctx: ViewContext,
    var text: TextSupplier = { LiteralTextComponent("Text") },
    var offsetX: Float = 0f,
    var offsetY: Float = 0f,
    private val textRenderer: TextRenderer
): ViewComponent(ctx), Drawable, LayoutElement {
    override fun onRender(ctx: Draw2dContext) {
        ctx.drawText(textRenderer, text(), vec2(offsetX, offsetY), DefaultZIndexes.TEXT.value)
    }

    override fun measure(c: Constraints): Size {
        val text = text()
        return Size(textRenderer.getWidth(text), textRenderer.getHeight(text))
    }
}