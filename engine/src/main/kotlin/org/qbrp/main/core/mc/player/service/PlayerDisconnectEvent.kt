package org.qbrp.main.core.mc.player.service

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.util.ActionResult
import org.qbrp.main.core.mc.player.ServerPlayerObject
import org.qbrp.main.core.mc.player.PlayersAPI

fun interface PlayerDisconnectEvent {
    fun onDisconnect(player: ServerPlayerObject, manager: PlayersAPI)

    companion object {
        val EVENT: Event<PlayerDisconnectEvent> = EventFactory.createArrayBacked(
            PlayerDisconnectEvent::class.java,
            { listeners: Array<out PlayerDisconnectEvent> ->
                PlayerDisconnectEvent { message, manager ->
                    for (listener in listeners) {
                        listener.onDisconnect(message, manager)
                    }
                    ActionResult.PASS
                }
            }
        )
    }
}