package org.qbrp.client.core.mouse

import net.minecraft.client.MinecraftClient
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.qbrp.client.core.mouse.events.MouseClickEvent
import org.qbrp.client.core.mouse.events.MouseDraggedEvent
import org.qbrp.client.core.mouse.events.MouseReleasedEvent
import org.qbrp.client.render.dataformats.Vec2
import org.qbrp.client.render.dataformats.vec2
import org.qbrp.main.core.events.EventContext
import org.qbrp.main.core.events.allFalse
import org.qbrp.main.core.events.collect

object MouseMixinAccess: KoinComponent {
    private val mouseState: MouseState by inject()
    private val eventContext: EventContext by inject()
    private val scale get() = MinecraftClient.getInstance().window.scaleFactor.toFloat()

    private fun getVec(x: Float, y: Float): Vec2 = (vec2(x, y).divide(scale))

    fun isMouseLocked() = mouseState.isLocked

    fun emitMouseClickEvent(x: Float, y: Float, button: Int): Boolean = with(eventContext) {
        MouseClickEvent(getVec(x, y), button, mouseState).collect<Boolean>().allFalse()
    }

    fun emitMouseReleasedEvent(x: Float, y: Float, button: Int): Boolean = with(eventContext) {
        MouseReleasedEvent(getVec(x, y), button, mouseState).collect<Boolean>().allFalse()
    }

    fun emitMouseDraggedEvent(x: Float, y: Float): Boolean = with(eventContext) {
        MouseDraggedEvent(getVec(x, y), mouseState).collect<Boolean>().allFalse()
    }
}