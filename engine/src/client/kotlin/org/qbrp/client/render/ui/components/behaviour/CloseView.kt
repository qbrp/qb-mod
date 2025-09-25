package org.qbrp.client.render.ui.components.behaviour

import org.koin.core.context.GlobalContext
import org.qbrp.client.render.dataformats.Vec2
import org.qbrp.client.render.ui.view.View
import org.qbrp.client.render.ui.view.ViewManager

class CloseView(val skip: Boolean = false): ClickListener {
    override fun onClicked(view: View, button: Int, pos: Vec2): Boolean {
        view.parent.removeView(view)
        return !skip
    }
}

class CloseWidget(val id: String, val viewManager: ViewManager = GlobalContext.get().get()): ClickListener {
    override fun onClicked(view: View, button: Int, pos: Vec2): Boolean {
        viewManager.closeView(id)
        return true
    }
}