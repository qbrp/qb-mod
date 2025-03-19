package org.qbrp.core.game.player.registration

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import org.qbrp.core.game.player.PlayerManager
import org.qbrp.core.game.player.ServerPlayerSession
import org.qbrp.engine.chat.core.messages.ChatMessage

fun interface PlayerRegistrationCallback {
    fun onRegister(session: ServerPlayerSession, manager: PlayerManager)

    companion object {
        val EVENT: Event<PlayerRegistrationCallback> = EventFactory.createArrayBacked(
            PlayerRegistrationCallback::class.java,
            { listeners: Array<out PlayerRegistrationCallback> ->
                PlayerRegistrationCallback { message, manager ->
                    for (listener in listeners) {
                        listener.onRegister(message, PlayerManager)
                    }
                    ActionResult.PASS
                }
            }
        )
    }
}