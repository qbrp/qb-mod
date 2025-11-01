package org.qbrp.client.controls

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import org.koin.core.context.GlobalContext
import org.qbrp.client.core.notifications.ClientNotificationSender
import org.qbrp.client.network.transport.sendC2SPacket
import org.qbrp.main.core.Core
import org.qbrp.main.core.events.EventType
import org.qbrp.main.core.events.emit
import org.qbrp.main.core.events.on
import org.qbrp.main.engine.keybind.KeybindId
import org.qbrp.main.network.player.KEYBIND_ACTIVATION_CHANNEL
import org.qbrp.main.network.player.KeybindActivationPacket
import org.qbrp.main.network.player.Notification
import kotlin.getValue

sealed class KeyBindModifier {
    object Control : KeyBindModifier()
}

fun sendKeybindSignalToServer(name: KeybindId) {
    KEYBIND_ACTIVATION_CHANNEL
        .sendC2SPacket(KeybindActivationPacket(name))
}

private fun handleKeyPress(
    id: KeybindId,
    modifiers: List<KeyBindModifier>,
    action: () -> Unit,
    notifications: ClientNotificationSender
) {
    if (MinecraftClient.getInstance().world == null) return
    val requireControl = modifiers.contains(KeyBindModifier.Control)
    if (requireControl && !Screen.hasControlDown()) return
    try {
        KeyBindEvent().emit(EventType<KeyBindEvent>(id.toEventId())) { result ->
            if (!result.cancelled) {
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

private fun registerKeyPressHandler(
    keybinding: KeyBinding,
    id: KeybindId,
    modifiers: List<KeyBindModifier>,
    action: () -> Unit
) {
    val clientNotificationSender: ClientNotificationSender by GlobalContext.get().inject()
    ClientTickEvents.END_CLIENT_TICK.register { client ->
        while (keybinding.wasPressed()) {
            handleKeyPress(id, modifiers, action, clientNotificationSender)
        }
    }
}

private fun registerDirectKeyPressHandler(
    id: KeybindId,
    key: Int,
    modifiers: List<KeyBindModifier>,
    action: () -> Unit
) {
    val clientNotificationSender: ClientNotificationSender by GlobalContext.get().inject()
    on<KeyPressEvent> {
        if (this.key == key) {
            handleKeyPress(id, modifiers, action, clientNotificationSender)
        }
    }
}

fun registerKeyBinding(
    keybinding: KeyBinding,
    id: KeybindId,
    modifiers: List<KeyBindModifier>,
    action: () -> Unit
) {
    KeyBindingHelper.registerKeyBinding(keybinding)
    registerKeyPressHandler(keybinding, id, modifiers, action)
}

fun createKeybinding(
    action: String,
    code: Int,
    type: InputUtil.Type = InputUtil.Type.KEYSYM,
    category: String = "qbrp"
): KeyBinding {
    return KeyBinding(action, type, code, category)
}

private fun registerPressReleaseHandler(
    keybinding: KeyBinding,
    id: KeybindId,
    onPress: () -> Unit,
    onRelease: () -> Unit
) {
    var wasPressed = false
    ClientTickEvents.END_CLIENT_TICK.register { client ->
        val pressed = keybinding.isPressed
        if (pressed && !wasPressed) {
            onPress.invoke()
            sendKeybindSignalToServer(id)
        } else if (!pressed && wasPressed) {
            onRelease.invoke()
        }
        wasPressed = pressed
    }
}

fun registerKeybinding(
    action: String,
    code: Int,
    id: KeybindId,
    type: InputUtil.Type = InputUtil.Type.KEYSYM,
    category: String = Core.MOD_ID,
    modifiers: List<KeyBindModifier> = listOf(),
    direct: Boolean = false,
    method: () -> Unit
) {
    if (direct) {
        registerDirectKeyPressHandler(id, code, modifiers, method)
    } else {
        registerKeyBinding(
            createKeybinding(action, code, type, category), id, modifiers, method,
        )
    }
}

fun registerKeybinding(
    action: String,
    code: Int,
    id: KeybindId,
    onPress: () -> Unit,
    onRelease: () -> Unit,
    type: InputUtil.Type = InputUtil.Type.KEYSYM,
    category: String = Core.MOD_ID
) {
    registerPressReleaseHandler(
        createKeybinding(action, code, type, category),
        id,
        onPress,
        onRelease
    )
}