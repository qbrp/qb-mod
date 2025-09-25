package org.qbrp.client.render.screen.event

import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.main.core.events.Event

data class ScreenRenderPipeline(val context: Draw2dContext): Event