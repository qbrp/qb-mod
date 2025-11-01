package org.qbrp.client.core.mouse

import net.minecraft.client.MinecraftClient
import net.minecraft.client.Mouse
import org.qbrp.client.render.dataformats.Vec2
import org.qbrp.client.render.dataformats.vec2
import org.qbrp.client.render.window.Window

class MouseState(private val minecraftClient: MinecraftClient, private val window: Window) {
    private val mcMouse get() = minecraftClient.mouse
    val pos: Vec2 get() = update()
    val posNormalized get() = vec2((1f / window.widthDp.toFloat()) * pos.x,  (1f / window.heightDp.toFloat()) * pos.y)
    val isLocked: Boolean get() = mcMouse.isCursorLocked || minecraftClient.currentScreen != null

    private fun update(): Vec2 {
        val client = MinecraftClient.getInstance()
        val rawX = client.mouse.x
        val rawY = client.mouse.y
        val fbWidth = client.window.framebufferWidth.toDouble()
        val fbHeight = client.window.framebufferHeight.toDouble()
        val guiWidth = client.window.scaledWidth.toDouble()
        val guiHeight = client.window.scaledHeight.toDouble()
        val mouseX = (rawX * guiWidth / fbWidth)
        val mouseY = (rawY * guiHeight / fbHeight)
        return vec2(mouseX.toFloat(), mouseY.toFloat())
    }

    fun lock() {
        mcMouse.lockCursor()
    }

    fun unlock() {
        mcMouse.updateMouse()
        mcMouse.unlockCursor()
    }

    fun toggleLock() {
        if (isLocked) {
            unlock()
        } else {
            lock()
        }
    }
}