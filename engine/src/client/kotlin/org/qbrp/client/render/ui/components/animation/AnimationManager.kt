package org.qbrp.client.render.ui.components.animation

import org.qbrp.client.render.Drawable
import org.qbrp.client.render.animation.Animation
import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.client.render.ui.view.ViewComponent
import org.qbrp.client.render.ui.view.ViewContext

class AnimationManager(ctx: ViewContext): ViewComponent(ctx), Drawable {
    private val animations: MutableList<Animation> = mutableListOf()
    var currentDrawContext: Draw2dContext? = null

    fun addAnimation(a: Animation, start: Boolean = true) {
        animations.add(a)
        if (start) a.start()
    }

    override fun onRender(ctx: Draw2dContext) {
        animations.forEach { a ->
            if (a.isRunning) a.tick(ctx.tickDelta * 20f)
        }
        currentDrawContext = ctx
    }
}