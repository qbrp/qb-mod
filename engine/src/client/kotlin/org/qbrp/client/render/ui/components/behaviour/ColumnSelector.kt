package org.qbrp.client.render.ui.components.behaviour

import org.qbrp.client.render.ui.components.layout.grid.AutoGridLayout
import org.qbrp.client.render.ui.components.layout.grid.GridPosition
import org.qbrp.client.render.ui.components.transform.RectTransform
import org.qbrp.client.render.ui.view.View
import org.qbrp.client.render.ui.view.ViewComponent
import org.qbrp.main.core.components.extensions.require

class ColumnSelector: TriggerSelector {
    override fun getViews(component: ViewComponent): List<View> {
        val viewColumn = component.view.require<GridPosition>().row
        return component.view.parent.require<AutoGridLayout>().elems.filter { it.gp.row == viewColumn }.map { it.view }
    }
}