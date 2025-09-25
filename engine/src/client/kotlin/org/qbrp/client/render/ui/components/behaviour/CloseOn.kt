package org.qbrp.client.render.ui.components.behaviour

import org.qbrp.client.render.Drawable
import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.client.render.ui.view.ViewComponent
import org.qbrp.client.render.ui.view.ViewContext
import org.qbrp.main.core.components.extensions.add

typealias ClosePostHandle = (CloseOn) -> Unit

class CloseOn(ctx: ViewContext, val trigger: CloseTrigger, val postHandle: ClosePostHandle = {}): ViewComponent(ctx), Drawable {
    var initialized: Boolean = false

    override fun onRender(ctx: Draw2dContext) {
        if (!initialized) { view.add(trigger); initialized = true }
        if (trigger.shouldClose) {
            view.parent.removeView(view)
            postHandle.invoke(this)
        }
    }
}