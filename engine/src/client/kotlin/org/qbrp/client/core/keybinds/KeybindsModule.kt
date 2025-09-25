package org.qbrp.client.core.keybinds

import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.qbrp.client.core.networking.preparation.SyncPreparation
import org.qbrp.client.core.networking.v2.ClientPacketSender
import org.qbrp.client.core.notifications.ClientNotificationSender
import org.qbrp.main.core.events.EventContext
import org.qbrp.main.core.events.emit
import org.qbrp.main.core.events.on
import org.qbrp.main.core.events.withEventContext
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.keybinds.KeybindActivationPacket
import org.qbrp.main.core.keybinds.KeybindId
import org.qbrp.main.core.keybinds.ServerKeybinds
import org.qbrp.main.core.notifications.entity.Notification

@Autoload(Priorities.HIGHEST, env = EnvType.CLIENT)
class KeybindsModule: EngineModule("keybinds"), ClientKeybindsAPI {
    private val keybinds = mutableMapOf<KeybindId, KeyBinding>()
    private val hiddenKeybinds = mutableMapOf<KeybindId, KeyBinding>()
    private val keyStates = mutableMapOf<KeybindId, Boolean>()

    private val notifications: ClientNotificationSender by inject()
    private val syncPreparation: SyncPreparation by inject()

    override fun getKoinModule(): Module = onlyApi<ClientKeybindsAPI>(this)

    override fun onEnable() {
        ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
            clearHiddenKeyBindings()
        }
    }

    fun clearHiddenKeyBindings() = hiddenKeybinds.clear()

    private val packetSender by inject<ClientPacketSender>()
    fun sendKeybindSignalToServer(name: KeybindId) {
        packetSender.send(KeybindActivationPacket(name), ServerKeybinds.CHANNEL)
    }

    fun registerHiddenKeyBinding(keybinding: KeyBinding, id: KeybindId, action: () -> Unit) {
        hiddenKeybinds[id] = keybinding.also {
            registerKeyPressHandler(it, id, listOf(), action)
        }
    }

    context(event: EventContext)
    private fun handleKeyPress(id: KeybindId, modifiers: List<KeyBindModifier>, action: () -> Unit) {
        if (!syncPreparation.isReady) return
        val requireControl = modifiers.contains(KeyBindModifier.Control)
        if (requireControl && !Screen.hasControlDown()) return
        try {
            KeyBindEvent().emit(withId = id.toEventId()) {
                if (!cancelled) {
                    action.invoke()
                    sendKeybindSignalToServer(id)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            val notification = Notification.error("Ошибка", "Обработка нажатия клавиши", e)
            notifications.send(notification)
        }
    }

    private fun registerKeyPressHandler(keybinding: KeyBinding, id: KeybindId, modifiers: List<KeyBindModifier>, action: () -> Unit) =
        withEventContext {
            ClientTickEvents.END_CLIENT_TICK.register { client ->
                while (keybinding.wasPressed()) {
                    handleKeyPress(id, modifiers, action)
                }
            }
        }

    private fun registerDirectKeyPressHandler(id: KeybindId, key: Int, modifiers: List<KeyBindModifier>, action: () -> Unit) =
        withEventContext {
            on<KeyPressEvent> {
                if (this.key == key) {
                    handleKeyPress(id, modifiers, action)
                }
            }
        }

    fun registerKeyBinding(
        keybinding: KeyBinding,
        id: KeybindId,
        modifiers: List<KeyBindModifier>,
        action: () -> Unit
    ) {
        keybinds[id] = keybinding.also {
            KeyBindingHelper.registerKeyBinding(it)
            registerKeyPressHandler(it, id, modifiers, action)
        }
    }

    fun createKeybinding(action: String, code: Int, type: InputUtil.Type = InputUtil.Type.KEYSYM, category: String = "qbrp"): KeyBinding {
        return KeyBinding(action, type, code, category,)
    }

    fun keybindExists(id: KeybindId): Boolean = keybinds.containsKey(id)
    fun hiddenKeybindExists(id: KeybindId): Boolean = hiddenKeybinds.containsKey(id)

    fun getKeybinding(id: KeybindId): KeyBinding {
        return keybinds[id]!!
    }

    private fun registerPressReleaseHandler(
        keybinding: KeyBinding,
        id: KeybindId,
        onPress: () -> Unit,
        onRelease: () -> Unit
    ) {
        keyStates[id] = false
        ClientTickEvents.END_CLIENT_TICK.register { client ->
            val pressed = keybinding.isPressed
            val wasPressed = keyStates[id] == true
            if (pressed && !wasPressed) {
                onPress.invoke()
                sendKeybindSignalToServer(id)
            } else if (!pressed && wasPressed) {
                onRelease.invoke()
            }
            keyStates[id] = pressed
        }
    }

    override fun registerKeybinding(
        action: String,
        code: Int,
        id: KeybindId,
        type: InputUtil.Type,
        category: String,
        modifiers: List<KeyBindModifier>,
        direct: Boolean,
        method: () -> Unit,
    ) {
        if (direct) {
            registerDirectKeyPressHandler(id, code, modifiers, method)
        } else {
            registerKeyBinding(
                createKeybinding(action, code, type, category), id, modifiers, method,
            )
        }
    }

    override fun registerKeybinding(
        action: String,
        code: Int,
        id: KeybindId,
        onPress: () -> Unit,
        onRelease: () -> Unit,
        type: InputUtil.Type,
        category: String
    ) {
        registerPressReleaseHandler(
            createKeybinding(action, code, type, category),
            id,
            onPress,
            onRelease
        )
    }
}