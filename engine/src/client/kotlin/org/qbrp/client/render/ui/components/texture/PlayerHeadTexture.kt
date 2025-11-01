package org.qbrp.client.render.ui.components.texture

import org.koin.core.context.GlobalContext
import org.qbrp.client.render.Drawable
import org.qbrp.client.render.heads.PlayerHeadsRenderer
import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.client.render.ui.components.layout.Constraints
import org.qbrp.client.render.ui.components.layout.LayoutElement
import org.qbrp.client.render.ui.components.layout.Size
import org.qbrp.client.render.ui.view.ViewComponent
import org.qbrp.client.render.ui.view.ViewContext
import org.qbrp.main.engine.players.model.PlayerProfile

class PlayerHeadTexture(
    ctx: ViewContext,
    val profile: PlayerProfile,
    val scale: Float = 1f,
    private val renderer: PlayerHeadsRenderer = GlobalContext.get().get()
) : ViewComponent(ctx),
    Drawable, LayoutElement {
    override fun onRender(ctx: Draw2dContext) {
        renderer.renderHeadTexture(ctx, profile, 0, 0, scale)
    }

    override fun measure(c: Constraints): Size {
        return Size(8f * scale, 8f * scale)
    }
}