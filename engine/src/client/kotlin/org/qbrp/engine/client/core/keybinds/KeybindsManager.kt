package org.qbrp.engine.client.core.keybinds

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW
import org.qbrp.engine.client.EngineClient
import org.qbrp.engine.client.engine.chat.ClientChatAPI
import org.qbrp.engine.client.render.Render
import org.qbrp.engine.client.system.networking.ClientNetworkManager

class KeybindsManager {
    private val keybinds = mutableMapOf<String, KeyBinding>()
    private val hiddenKeybinds = mutableMapOf<String, KeyBinding>()

    fun registerKeyBindings() {
        registerKeyBinding(
            createKeybinding(
                "Появиться",
                GLFW.GLFW_KEY_X,
            ), "spectators_spawn") { }
        registerKeyBinding(
            createKeybinding(
                "Информация",
                GLFW.GLFW_KEY_G,
            ), "information") { }
        registerKeyBinding(
            createKeybinding(
                "Очистить чат от системных сообщений",
                GLFW.GLFW_KEY_L,
            ), "clear_system_msgs") {
            EngineClient.getAPI<ClientChatAPI>()?.clearSystemMessages()
        }
    }

    fun clearHiddenKeyBindings() = hiddenKeybinds.clear()

    fun sendKeybindSignalToServer(name: String) {
        ClientNetworkManager.sendSignal("key_bind_$name")
    }

    fun registerHiddenKeyBinding(keybinding: KeyBinding, id: String, action: () -> Unit) {
        hiddenKeybinds[id] = keybinding.also {
            registerKeyPressHandler(it, id, action)
        }
    }

    private fun registerKeyPressHandler(keybinding: KeyBinding, id: String, action: () -> Unit) {
        ClientTickEvents.END_CLIENT_TICK.register { client ->
            while (keybinding.wasPressed()) {
                action.invoke()
                sendKeybindSignalToServer(id)
            }
        }
    }

    fun registerKeyBinding(keybinding: KeyBinding, id: String, action: () -> Unit) {
        keybinds[id] = keybinding.also {
            KeyBindingHelper.registerKeyBinding(it)
            registerKeyPressHandler(it, id, action)
        }
    }

    fun createKeybinding(action: String, code: Int, type: InputUtil.Type = InputUtil.Type.KEYSYM, category: String = "qbrp"): KeyBinding {
        return KeyBinding(action, type, code, category,)
    }

    fun keybindExists(id: String): Boolean = keybinds.containsKey(id)
    fun hiddenKeybindExists(id: String): Boolean = hiddenKeybinds.containsKey(id)

    fun getKeybinding(id: String): KeyBinding {
        return keybinds[id]!!
    }
}