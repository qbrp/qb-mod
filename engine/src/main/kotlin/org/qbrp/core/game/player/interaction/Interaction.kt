package org.qbrp.core.game.player.interaction

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import org.qbrp.core.game.player.PlayerManager
import org.qbrp.core.game.player.ServerPlayerSession
import org.qbrp.core.keybinds.ServerKeybindCallback
import org.qbrp.core.keybinds.ServerKeybinds
import org.qbrp.system.utils.format.Format.asMiniMessage

open class Interaction(val keybind: String) {
    lateinit var mode: InteractionMode

    init {
        registerInvoker()
    }

    open fun registerInvoker() {
        ServerKeybinds.registerKeybindReceiver(keybind)
        ServerKeybindCallback.getOrCreateEvent(keybind).register { player ->
            invoke(player)
            ActionResult.PASS
        }
    }

    fun blockedCallback(player: ServerPlayerSession, message: String) {
        callback(player, "<red>Взаимодействие заблокировано: $message</red>")
    }

    fun successCallback(player: ServerPlayerSession, message: String) {
        callback(player, "<green>$message</green>")
    }

    fun callback(player: ServerPlayerSession, message: String) {
        player.entity.sendMessage(message.asMiniMessage())
    }

    open fun invoke(player: ServerPlayerSession, intent: Intent) {
        InteractEvents.getOrCreate(mode.name, keybind).invoker().onInteraction(player, intent)
    }

    open fun invoke(player: ServerPlayerEntity) {
        PlayerManager.getPlayerSession(player).let {
            invoke(it, it.interactionManager.intent)
        }
    }
}
