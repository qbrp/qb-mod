package org.qbrp.client.render.ui.components.window

import org.qbrp.client.render.ui.view.View
import org.qbrp.client.render.ui.view.ViewComponent
import org.qbrp.client.render.ui.view.ViewContext

class WindowManager(ctx: ViewContext, val views: Views): ViewComponent(ctx) {
    val currentView: View get() = views.variants[views.currentViewIndex]

    init {
        view.addView(currentView)
    }

    private var lastViewIndex = 0
    fun setView(viewToSet: View) {
        lastViewIndex = view.children.indexOf(currentView)
        if (view == currentView) return
        view.removeView(currentView)
        view.setViewAtIndex(viewToSet, lastViewIndex)
    }
}