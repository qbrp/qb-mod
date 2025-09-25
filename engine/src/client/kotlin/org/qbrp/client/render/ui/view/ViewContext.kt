package org.qbrp.client.render.ui.view

import org.koin.core.context.GlobalContext
import org.qbrp.client.core.mouse.MouseState
import org.qbrp.client.render.ui.UiContext
import org.qbrp.main.core.components.InstantiationContext

class ViewContext(override val componentManager: ViewState) : InstantiationContext<ViewState>, ViewState by componentManager {
    val mouse: MouseState by lazy { GlobalContext.get().get() }
}
