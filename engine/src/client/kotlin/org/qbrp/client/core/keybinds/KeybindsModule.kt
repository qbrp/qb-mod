package org.qbrp.client.core.keybinds

import eu.midnightdust.lib.config.MidnightConfig
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import org.koin.core.module.Module
import org.lwjgl.glfw.GLFW
import org.qbrp.main.core.Core
import org.qbrp.client.core.networking.ClientNetworkUtil
import org.qbrp.main.core.keybinds.ServerKeyBind
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.LoadPriority
import org.qbrp.main.core.modules.QbModule
import org.qbrp.main.core.utils.networking.info.ServerInformationGetEvent
import org.qbrp.main.core.utils.networking.messages.components.Cluster

@Autoload(LoadPriority.LOWEST, env = EnvType.CLIENT)
class KeybindsModule: QbModule("keybinds"), ClientKeybindsAPI {
    private val keybinds = mutableMapOf<String, KeyBinding>()
    private val hiddenKeybinds = mutableMapOf<String, KeyBinding>()

    override fun getKoinModule(): Module = onlyApi<ClientKeybindsAPI>(this)

    override fun onEnable() {
        ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
            clearHiddenKeyBindings()
        }
        ServerInformationGetEvent.EVENT.register {
            val serverKeybinds = it.getComponentData<List<Cluster>>("core.keybinds")
                ?.map {
                    val data = it.getData()
                    ServerKeyBind(
                        data.getComponentData<String>("id")!!,
                        data.getComponentData<Int>("defaultKey")!!,
                        data.getComponentData<String>("name")!!
                    )
                }
                ?: emptyList()
            serverKeybinds.forEach {
                if (!keybindExists(it.id)) {
                    val keybind = createKeybinding(it.id, it.defaultKey)
                    registerHiddenKeyBinding(keybind, it.id) { }
                }
            }
        }

    }

    fun registerKeyBindings() {
        registerKeyBinding(
            createKeybinding(
                "Информация",
                GLFW.GLFW_KEY_G,
            ), "information") {
        } //TODO: В controls
        registerKeyBinding(
            createKeybinding(
                "Настройки",
                GLFW.GLFW_KEY_EQUAL,
            ), "settings") {
            MinecraftClient.getInstance().setScreen(MidnightConfig.getScreen(MinecraftClient.getInstance().currentScreen, Core.MOD_ID))
        }
    }

    fun clearHiddenKeyBindings() = hiddenKeybinds.clear()

    fun sendKeybindSignalToServer(name: String) {
        ClientNetworkUtil.sendSignal("key_bind_$name")
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

    override fun registerKeybinding(
        action: String,
        code: Int,
        id: String,
        method: () -> Unit,
        type: InputUtil.Type,
        category: String
    ) {
        registerKeyBinding(
            createKeybinding(action, code, type, category), id, method,
        )
    }
}