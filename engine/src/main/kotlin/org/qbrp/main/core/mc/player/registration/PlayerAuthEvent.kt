package org.qbrp.main.core.mc.player.registration

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.util.ActionResult
import org.qbrp.main.core.mc.player.PlayerObject
import org.qbrp.main.core.mc.player.PlayersAPI

fun interface PlayerAuthEvent {
    fun onRegister(session: PlayerObject, manager: PlayersAPI)

    companion object {
        val EVENT: Event<PlayerAuthEvent> = EventFactory.createArrayBacked(
            PlayerAuthEvent::class.java,
            { listeners: Array<out PlayerAuthEvent> ->
                PlayerAuthEvent { message, manager ->
                    for (listener in listeners) {
                        listener.onRegister(message, manager)
                    }
                    ActionResult.PASS
                }
            }
        )
    }
}