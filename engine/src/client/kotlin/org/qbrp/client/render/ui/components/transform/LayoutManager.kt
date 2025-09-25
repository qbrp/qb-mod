package org.qbrp.client.render.ui.components.transform

import org.qbrp.client.render.ui.components.layout.Constraints
import org.qbrp.client.render.ui.components.layout.LayoutGroup
import org.qbrp.client.render.ui.components.layout.Rect
import org.qbrp.client.render.ui.components.layout.Size
import org.qbrp.client.render.ui.view.View

class LayoutManager(val views: List<View>, val layout: LayoutGroup, val rect: Rect) {
    fun measure(constraints: Constraints): Size {
        return layout.measure(views, constraints)
    }

    fun arrange() {
        return layout.arrange(views, rect)
    }

    fun invalidate(constraints: Constraints) {
        measure(constraints)
        arrange()
    }
}