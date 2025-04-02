package org.qbrp.core.game.player.interaction

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import org.qbrp.core.game.player.PlayerManager
import org.qbrp.core.game.player.ServerPlayerSession
import org.qbrp.core.keybinds.ServerKeybindCallback
import org.qbrp.core.keybinds.ServerKeybinds

open class Interaction(val keybind: String) {

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

    fun invoke(player: ServerPlayerSession, intent: Intent) {
        //InteractEvents.getOrCreate(keybind).invoker().onInteraction(player, intent)
    }

    fun invoke(player: ServerPlayerEntity) {
        PlayerManager.getPlayerSession(player).let {
            //invoke(it, it.interactionManager.intent)
        }
    }
}
