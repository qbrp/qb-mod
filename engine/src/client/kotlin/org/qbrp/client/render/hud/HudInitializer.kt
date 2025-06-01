package org.qbrp.client.render.hud

import icyllis.modernui.core.Core
import icyllis.modernui.fragment.Fragment
import icyllis.modernui.mc.MuiModApi
import icyllis.modernui.mc.ScreenCallback
import icyllis.modernui.mc.UIManager
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.client.gui.screen.GameMenuScreen
import net.minecraft.client.gui.screen.Screen

class HudInitializer : MuiModApi.OnScreenChangeListener {
    var hud: Screen? = null

    init {
        // Регистрируем слушатель изменений экрана
        MuiModApi.addOnScreenChangeListener(this)
    }

    // Метод для создания и открытия HUD
    fun initScreen() {
        TODO()
        if (hud == null) {
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
            //hud = constructor.newInstance(uiManager, GameScreen(), null, null, null) as Screen
        }
        val minecraft = MinecraftClient.getInstance()
        hud?.init(minecraft, minecraft.window.scaledWidth, minecraft.window.scaledHeight)
        minecraft.setScreen(hud)
        println("HUD initialized and opened")
    }

    // Метод для закрытия HUD
    private fun removeHUD() {
        if (hud != null) {
            val uiManagerField = UIManager::class.java.getDeclaredField("sInstance")
            uiManagerField.isAccessible = true
            val uiManager = uiManagerField.get(null) as UIManager
            val mScreenField = UIManager::class.java.getDeclaredField("mScreen")
            mScreenField.isAccessible = true
            val currentScreen = mScreenField.get(uiManager)
            mScreenField.set(uiManager, hud)
            uiManager.removed(hud as Screen)
            mScreenField.set(uiManager, currentScreen)
            println("HUD removed")
            hud = null
        }
    }

    // Обработка изменения экрана
    override fun onScreenChange(oldScreen: Screen?, newScreen: Screen?) {
        if (hud == null) {
            if (newScreen == null) {
                initScreen()
            }
        } else {
            if (newScreen is ChatScreen || newScreen is GameMenuScreen) {
                removeHUD()
            }
        }
    }
}