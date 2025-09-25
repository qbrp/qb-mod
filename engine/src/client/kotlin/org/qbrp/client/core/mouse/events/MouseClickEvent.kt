package org.qbrp.client.core.mouse.events

import org.qbrp.client.core.mouse.MouseState
import org.qbrp.client.render.dataformats.Vec2
import org.qbrp.main.core.events.Event

data class MouseClickEvent(val pos: Vec2, val button: Int, val state: MouseState): Event