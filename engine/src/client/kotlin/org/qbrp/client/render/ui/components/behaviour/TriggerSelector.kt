package org.qbrp.client.render.ui.components.behaviour

import org.qbrp.client.render.ui.view.View
import org.qbrp.client.render.ui.view.ViewComponent

interface TriggerSelector {
    fun getViews(component: ViewComponent): List<View>
}