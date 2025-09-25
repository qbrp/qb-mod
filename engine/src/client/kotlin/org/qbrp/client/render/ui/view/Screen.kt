package org.qbrp.client.render.ui.view

import net.minecraft.client.MinecraftClient
import org.qbrp.client.render.dataformats.MutableVec2
import org.qbrp.client.render.dataformats.vec2

open class Screen: View() {
    private val client: MinecraftClient = MinecraftClient.getInstance()
    override var width: Float
        get() = client.window.scaledWidth.toFloat()
        set(value) {}
    override var height: Float
        get() = client.window.scaledHeight.toFloat()
        set(value) {}
    override var _pos: MutableVec2
        get() = MutableVec2(0f, 0f)
        set(value) {}
}