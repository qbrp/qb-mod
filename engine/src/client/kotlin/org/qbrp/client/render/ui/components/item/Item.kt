package org.qbrp.client.render.ui.components.item

import org.qbrp.main.engine.items.ModelId
import org.qbrp.client.render.Drawable
import org.qbrp.client.render.item.model.data.ItemDisplay
import org.qbrp.client.render.item.model.data.ItemTooltip
import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.client.render.ui.view.ViewComponent
import org.qbrp.client.render.ui.view.ViewContext
import org.qbrp.main.engine.text.components.TextComponent
import org.qbrp.main.engine.text.emptyText

class Item(ctx: ViewContext, modelId: ModelId, val scale: Float = 16f): ViewComponent(ctx), Drawable {
    private val display = createPlainItemDisplay(modelId)
    override fun onRender(ctx: Draw2dContext) {
        ctx.drawItem(display, 0f, 0f, scale = scale)
    }

    private fun createPlainItemDisplay(id: ModelId): ItemDisplay {
        return object : ItemDisplay {
            override val model: ModelId get() = id
            override val name: TextComponent get() = emptyText()
            override val tooltip: ItemTooltip? get() = null
        }
    }
}