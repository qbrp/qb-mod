package org.qbrp.client.render.ui.components.behaviour

import org.qbrp.client.render.ui.view.View
import org.qbrp.client.render.ui.view.ViewComponent
import org.qbrp.client.render.ui.view.ViewContext

fun interface CloseListener {
    fun onViewClosed(view: View)
}

class OnClose(ctx: ViewContext, val listener: CloseListener): ViewComponent(ctx) {
    override fun onRemoved() {
        listener.onViewClosed(view)
    }
}