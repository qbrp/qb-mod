package org.qbrp.client.render.ui.components.behaviour

import org.qbrp.client.render.ui.view.ViewState
import org.qbrp.main.core.components.Component

interface CloseTrigger: Component<ViewState> {
    val shouldClose: Boolean
}