package org.qbrp.client.render.ui.view

import org.qbrp.main.core.components.extensions.add
import java.util.concurrent.CopyOnWriteArrayList

class ViewGroup internal constructor(private var closeAllOnClickOutView: Boolean = false,
                                     internal var removeCallback: () -> Unit = {},
                                     internal var destroyCallback: () -> Unit = {}) {
    internal var removeProcess: Boolean = false
    private val views: CopyOnWriteArrayList<View> = CopyOnWriteArrayList()
    fun add(view: View) {
        views.add(view)
        view.add(Member(view.componentContext, this))
    }

    fun destroy() {
        removeAll()
        destroyCallback.invoke()
    }

    fun removeAll() {
        removeProcess = true
        try {
            views.forEach { it.removeSelf() }
            views.clear()
            removeCallback.invoke()
        } finally {
            removeProcess = false
        }
    }

    fun onClick(x: Float, y: Float) {
        val views = views.toList()
        if (views.isEmpty()) return
        if (closeAllOnClickOutView && views.none { it.inViewGlobal(x, y) }) {
            removeAll()
        }
    }

    class Member(ctx: ViewContext, val group: ViewGroup): ViewComponent(ctx) {
        var removed = false
        override fun onRemoved() {
            if (!removed && !group.removeProcess) {
                removed = true
                group.removeAll()
            }
        }
    }
}