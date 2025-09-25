package org.qbrp.client.render.ui.view

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import org.qbrp.client.core.mouse.MouseState
import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.client.render.window.Window
import org.qbrp.main.core.components.Component

open class ViewComponent(ctx: ViewContext): Component<ViewState> {
    override var state: ViewState = ctx.componentManager
    protected val mouse: MouseState by lazy { ctx.mouse }
    val view: View get() = this@ViewComponent.state.parent
    open fun onTransform(ctx: Draw2dContext) {}
    open fun onInit() {}
    open fun onResize(client: MinecraftClient, window: Window) {}
    open fun onMouse(mX: Float, mY: Float) {}
    open fun onAddView(view: View) {}
    open fun onRemoved() {}
    open fun onTick() {}
    open fun onFrame() {}
}