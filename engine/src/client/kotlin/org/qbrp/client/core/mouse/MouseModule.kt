package org.qbrp.client.core.mouse

import net.fabricmc.api.EnvType
import net.minecraft.client.MinecraftClient
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.dsl.module
import org.lwjgl.glfw.GLFW
import org.qbrp.client.controls.KeyPressEvent
import org.qbrp.client.render.ui.utilview.GameMenu
import org.qbrp.main.core.events.on
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.modules.EngineModule
import kotlin.getValue

@Autoload(priority = Priorities.HIGHEST - 1, env = EnvType.CLIENT)
class MouseModule : EngineModule("mouse") {
    override fun getKoinModule(): Module = module {
        single { MouseState(MinecraftClient.getInstance(), get()) }
    }

    private val gameMenu by inject<GameMenu>()
    private val mouseState: MouseState by inject()

    override fun onEnable() {
        val client = MinecraftClient.getInstance()
        on<KeyPressEvent, Boolean> {
            if (key != GLFW.GLFW_KEY_ESCAPE) return@on false
            if (client.currentScreen == null) {
                if (!mouseState.isLocked) {
                    mouseState.lock()
                } else {
                    gameMenu.open()
                }
            }
            false
        }
    }
}