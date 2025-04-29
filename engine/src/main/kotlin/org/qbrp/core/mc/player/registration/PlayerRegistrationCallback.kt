package org.qbrp.core.mc.player.registration

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.util.ActionResult
import org.qbrp.core.mc.player.PlayerManager
import org.qbrp.core.mc.player.PlayerObject

fun interface PlayerRegistrationCallback {
    fun onRegister(session: PlayerObject, manager: PlayerManager)

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