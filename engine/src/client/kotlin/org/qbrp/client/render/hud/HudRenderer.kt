package org.qbrp.client.render.hud

import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import org.koin.core.component.get
import org.koin.dsl.module
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.QbModule

@Autoload(env = EnvType.CLIENT)
class HudRenderer: QbModule("hud") {
    override fun getKoinModule() = module {
        single { InventoryManager() }
    }

    override fun onEnable() {
        val heldItemRender = HeldItemRenderer(get())
        val inventories = get<InventoryManager>()
        HudRenderCallback.EVENT.register { ctx, delta ->
            val mousePos = getMousePos()
            heldItemRender.render(ctx, mousePos.first.toInt(), mousePos.second.toInt(), MinecraftClient.getInstance().textRenderer)
            inventories.onHudRender(ctx, mousePos.first.toInt(), mousePos.second.toInt(), delta)
        }
    }

    /** @see mixin.MouseMixin **/
    fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val mousePos = getMousePos()
        get<InventoryManager>().mouseClicked(mousePos.first, mousePos.second, button)
        return true
    }

    /** @see mixin.MouseMixin **/
    fun mouseDragged(mouseX: Double, mouseY: Double, button: Int) {
        val mousePos = getMousePos()
        get<InventoryManager>().mouseDragged(mousePos.first, mousePos.second, button)
    }

    /** @see mixin.MouseMixin **/
    fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val mousePos = getMousePos()
        return get<InventoryManager>().mouseReleased(mousePos.first, mousePos.second, button)
    }

    private fun getMousePos(): Pair<Double, Double> {
        val client = MinecraftClient.getInstance()
        val rawX = client.mouse.x
        val rawY = client.mouse.y
        val fbWidth = client.window.framebufferWidth.toDouble()
        val fbHeight = client.window.framebufferHeight.toDouble()
        val guiWidth = client.window.scaledWidth.toDouble()
        val guiHeight = client.window.scaledHeight.toDouble()
        val mouseX = (rawX * guiWidth / fbWidth)
        val mouseY = (rawY * guiHeight / fbHeight)
        return Pair<Double, Double>(mouseX, mouseY)
    }
}