package org.qbrp.main.core.mc.player.registration

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.util.ActionResult
import org.qbrp.main.core.mc.player.PlayersModule
import org.qbrp.main.core.mc.player.PlayerObject
import org.qbrp.main.core.mc.player.PlayersAPI

fun interface PlayerRegistrationCallback {
    fun onRegister(session: PlayerObject, manager: PlayersAPI)

    companion object {
        val EVENT: Event<PlayerRegistrationCallback> = EventFactory.createArrayBacked(
            PlayerRegistrationCallback::class.java,
            { listeners: Array<out PlayerRegistrationCallback> ->
                PlayerRegistrationCallback { message, manager ->
                    for (listener in listeners) {
                        listener.onRegister(message, manager)
                    }
                    ActionResult.PASS
                }
            }
        )
    }
}