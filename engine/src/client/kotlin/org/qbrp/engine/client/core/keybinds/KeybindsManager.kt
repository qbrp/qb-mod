package org.qbrp.engine.client.core.keybinds

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW
import org.qbrp.engine.client.EngineClient
import org.qbrp.engine.client.render.Render

class KeybindsManager {
    private val keybinds = mutableMapOf<String, KeyBinding>()
    init {
        registerKeyBinding(
            createKeybinding(
            "Открыть РП-чат",
            GLFW.GLFW_KEY_K,
        ), "rpChatOpen") {
            Render.HUD.openChat()
        }
        registerKeyBinding(
            createKeybinding(
                "Появиться",
                GLFW.GLFW_KEY_X,
            ), "spectatorsSpawn") {
            //TODO: Доделать обработку сообщений на серверной стороне
            EngineClient.spectatorsModule.spawnPlayer()
        }
    }

    fun registerKeyBinding(keybinding: KeyBinding, id: String, action: () -> Unit) {
        keybinds[id] = keybinding.also {
            KeyBindingHelper.registerKeyBinding(it)
            ClientTickEvents.END_CLIENT_TICK.register { tick ->
                while(it.wasPressed()) {
                    action.invoke()
                }
            }
        }
    }

    fun createKeybinding(action: String, code: Int, type: InputUtil.Type = InputUtil.Type.KEYSYM, category: String = "qbrp"): KeyBinding {
        return KeyBinding(action, type, code, category,)
    }

    fun getKeybinding(id: String): KeyBinding {
        return keybinds[id]!!
    }
}