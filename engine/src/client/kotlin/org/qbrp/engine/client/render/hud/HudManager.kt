package org.qbrp.engine.client.render.hud

import icyllis.modernui.fragment.Fragment
import icyllis.modernui.mc.MuiModApi
import icyllis.modernui.mc.ScreenCallback
import icyllis.modernui.mc.UIManager
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import org.lwjgl.glfw.GLFW

class HudManager : MuiModApi.OnScreenChangeListener {
    companion object {
        private var hud: Screen? = null
        private var hudOpened = false
    }

    var cachedWith = 0
    var cachedHeight = 0
    init {
        // Регистрируем слушатель изменений экрана
        MuiModApi.addOnScreenChangeListener(this)
        ClientLifecycleEvents.CLIENT_STARTED.register {
            initMainScreen()
        }
    }

    // Инициализация HUD один раз
    private fun initMainScreen() {
        val uiManager = UIManager.getInstance()
        val cl = Class.forName("icyllis.modernui.mc.fabric.SimpleScreen")
        val constructor = cl.getDeclaredConstructor(
            UIManager::class.java,
            Fragment::class.java,
            ScreenCallback::class.java,
            Screen::class.java,
            CharSequence::class.java
        )
        constructor.isAccessible = true
        hud = constructor.newInstance(uiManager, GameScreen(), null, null, null) as Screen
    }

    fun renderHud(drawContext: DrawContext, tickDelta: Float) {
        if (hudOpened && hud != null) {
            hud!!.render(drawContext, 0, 0, tickDelta)
        }
    }

    // Открытие HUD
    private fun openHud() {
        if (hud == null) { initMainScreen() }
        val minecraft = MinecraftClient.getInstance()
        hud?.init(minecraft, minecraft.window.scaledWidth, minecraft.window.scaledHeight)
        minecraft.setScreen(hud)
        hudOpened = true
        println("HUD opened")
    }

    // Закрытие HUD
    private fun closeHud() {
        if (hudOpened) {
            val minecraft = MinecraftClient.getInstance()
            if (minecraft.currentScreen == hud) {
                minecraft.setScreen(null)
            }
            hudOpened = false
            println("HUD closed")
        }
    }

    // Обработка изменения экрана
    override fun onScreenChange(screen: Screen?, newScreen: Screen?) {
        val minecraft = MinecraftClient.getInstance()
        if (minecraft.world != null) { // Проверяем, что игрок в мире
            if (newScreen == null && !hudOpened) {
                // Если новый экран null и HUD не открыт, открываем HUD
                openHud()
            } else if (newScreen != null && hudOpened) {
                // Если открылся новый экран и HUD открыт, закрываем HUD
                closeHud()
            }
        }
        println("Old: ${screen?.title?.string}, New: ${newScreen?.title?.string}")
    }


}