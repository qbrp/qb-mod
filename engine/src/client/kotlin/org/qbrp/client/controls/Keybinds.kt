package org.qbrp.client.controls

import eu.midnightdust.lib.config.MidnightConfig
import net.minecraft.client.MinecraftClient
import org.koin.core.Koin
import org.lwjgl.glfw.GLFW
import org.qbrp.client.core.mouse.MouseState
import org.qbrp.client.core.notifications.ClientNotificationSender
import org.qbrp.client.engine.chat.ClientChatAPI
import org.qbrp.client.engine.movement.Movement
import org.qbrp.client.engine.players.ClientPlayerManager
import org.qbrp.client.network.play.NetworkState
import org.qbrp.client.network.play.sendFlyAbilityPacket
import org.qbrp.main.core.Core
import org.qbrp.main.core.fabricmc.player.PlayerPermissions
import org.qbrp.main.engine.controls.entity.InteractionMode
import org.qbrp.main.engine.controls.entity.interactions.Intent
import org.qbrp.main.engine.keybind.KeybindControllerModule
import org.qbrp.main.engine.keybind.KeybindId
import org.qbrp.main.engine.physics.player.FLY_PERMISSION
import org.qbrp.main.engine.physics.player.fly
import org.qbrp.main.engine.physics.player.toggleFly
import org.qbrp.main.engine.text.miniMessage
import org.qbrp.main.network.player.Notification
import kotlin.getValue

internal fun registerKeybinds(koin: Koin) {
    val chatModule: ClientChatAPI by koin.inject()
    val mouseState: MouseState by koin.inject()
    val interactionModeManager: ClientInteractionModeManager by koin.inject()
    val interactionManager: ClientInteractionManager by koin.inject()
    val notificationSender: ClientNotificationSender by koin.inject()
    val clientPlayerManager: ClientPlayerManager by koin.inject()

    registerKeybinding(
        "Настройки",
        GLFW.GLFW_KEY_EQUAL,
        KeybindId("settings"),
        method = {
            MinecraftClient.getInstance().apply {
                setScreen(MidnightConfig.getScreen(currentScreen, Core.MOD_ID))
            }
        }
    )
    registerKeybinding(
        "Очистить чат от системных сообщений",
        GLFW.GLFW_KEY_L,
        KeybindId("clear_system_msgs"),
        method = {
            chatModule.clearSystemMessages()
        }
    )

    registerKeybinding(
        "Полёт",
        GLFW.GLFW_KEY_F,
        KeybindId("fly"),
        modifiers = listOf(KeyBindModifier.Control)
    ) {
        val permissions: PlayerPermissions = koin.get<NetworkState>().permissions ?: return@registerKeybinding
        val clientPlayer = clientPlayerManager.clientPlayer ?: return@registerKeybinding
        if (permissions.hasPermission(FLY_PERMISSION)) {
            clientPlayer.toggleFly()
            sendFlyAbilityPacket(clientPlayer.fly)
        } else {
            notificationSender.send(
                Notification.info(
                    "Невозможно применить полёт",
                    miniMessage("Недостаточно прав")
                )
            )
        }
    }

    registerKeybinding(
        "Появиться",
        GLFW.GLFW_KEY_X,
        KeybindId("spectators_spawn"),
        method = {}
    )

    registerKeybinding(
        "Разблокировать курсор",
        GLFW.GLFW_KEY_GRAVE_ACCENT,
        KeybindId("mouse-unlock"),
        method = {
            mouseState.toggleLock()
        }
    )

    registerKeybinding(
        "Творческий инвентарь",
        GLFW.GLFW_KEY_Y,
        KeybindControllerModule.CREATIVE_INVENTORIES,
        method = {}
    )

    registerKeybinding(
        "Показать руки",
        GLFW.GLFW_KEY_E,
        KeybindControllerModule.OPEN_HANDS_KEYBIND,
        method = {}
    )

    registerInteractionKeybinds(interactionManager)
    registerInteractionModeKeybinds(interactionModeManager)
}

val DEFAULT_INTERACTION_MODE_KEYBIND = KeybindId("default-interaction-mode")
val BUILDER_INTERACTION_MODE_KEYBIND = KeybindId("builder-interaction-mode")
val GM_INTERACTION_MODE_KEYBIND = KeybindId("gm-interaction-mode")
val CROW_INTERACTION_MODE_KEYBIND = KeybindId("crow-interaction-mode")
val NO_CLIP_KEYBIND = KeybindId("crow-interaction-mode")

private fun registerInteractionModeKeybinds(interactionModeManager: ClientInteractionModeManager) {
    registerKeybinding(
        "Стандартный режим",
        GLFW.GLFW_KEY_1,
        DEFAULT_INTERACTION_MODE_KEYBIND,
        modifiers = listOf(KeyBindModifier.Control),
        method = {
            interactionModeManager.setMode(InteractionMode.DEFAULT)
        },
        direct = true
    )

    registerKeybinding(
        "Строительный режим",
        GLFW.GLFW_KEY_2,
        BUILDER_INTERACTION_MODE_KEYBIND,
        modifiers = listOf(KeyBindModifier.Control),
        method = {
            interactionModeManager.setMode(InteractionMode.BUILDER)
        },
        direct = true
    )

    registerKeybinding(
        "Режим ГМа",
        GLFW.GLFW_KEY_3,
        GM_INTERACTION_MODE_KEYBIND,
        modifiers = listOf(KeyBindModifier.Control),
        method = {
            interactionModeManager.setMode(InteractionMode.GM)
        },
        direct = true
    )

    registerKeybinding(
        "Режим наблюдения",
        GLFW.GLFW_KEY_4,
        CROW_INTERACTION_MODE_KEYBIND,
        modifiers = listOf(KeyBindModifier.Control),
        method = {
            interactionModeManager.setMode(InteractionMode.CROW)
        },
        direct = true
    )

    registerKeybinding(
        "Переключение NoClip",
        GLFW.GLFW_KEY_TAB,
        NO_CLIP_KEYBIND,
        modifiers = listOf(KeyBindModifier.Control),
        method = {
            interactionModeManager.toggleNoClip()
        }
    )
}

/**
 * @see KeybindControllerModule.registerInteractionKeybinds
 */
private fun registerInteractionKeybinds(interactionManager: ClientInteractionManager) {
    registerKeybinding(
        "Взаимодействие",
        GLFW.GLFW_KEY_K,
        KeybindId("interact"),
        method = { interactionManager.call() }
    )

    registerKeybinding(
        "Альтернативный режим",
        GLFW.GLFW_MOD_ALT,
        KeybindControllerModule.TOGGLE_ALTERNATE_KEYBIND,
        method = {
            interactionManager.apply { alternate = !alternate }
        }
    )
    registerKeybinding(
        "Основной интент",
        GLFW.GLFW_KEY_1,
        KeybindControllerModule.DEFAULT_INTENT_KEYBIND,
        method = {
            interactionManager.intent = Intent.DEFAULT
        }
    )
    registerKeybinding(
        "Тонкий интент",
        GLFW.GLFW_KEY_2,
        KeybindControllerModule.FINE_INTENT_KEYBIND,
        method = {
            interactionManager.intent = Intent.FINE
        }
    )
    registerKeybinding(
        "Грубый интент",
        GLFW.GLFW_KEY_3,
        KeybindControllerModule.RUDE_INTENT_KEYBIND,
        method = {
            interactionManager.intent = Intent.ROUGH
        }
    )
}