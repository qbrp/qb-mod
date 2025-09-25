package org.qbrp.client.render.ui.components.layout

import org.qbrp.client.render.ui.view.ViewComponent
import org.qbrp.client.render.ui.view.ViewContext

class LayoutComponent(ctx: ViewContext, val layout: LayoutGroup): ViewComponent(ctx), LayoutGroup by layout