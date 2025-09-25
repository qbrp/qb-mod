package org.qbrp.client.render.ui.components.layout.grid

import org.qbrp.client.render.ui.view.ViewComponent
import org.qbrp.client.render.ui.view.ViewContext

class GridPosition(
    ctx: ViewContext,
    var col: Int = 0,
    var row: Int = 0,
    var colSpan: Int = 1,
    var rowSpan: Int = 1
) : ViewComponent(ctx)