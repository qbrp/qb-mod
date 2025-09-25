package org.qbrp.client.engine.auth

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.client.network.ClientPlayNetworkHandler
import org.qbrp.main.engine.players.auth.SessionData

fun interface ClientAuthEvent {
    fun onAuth(session: SessionData)

    companion object {
        val EVENT: Event<ClientAuthEvent> = EventFactory.createArrayBacked(
            ClientAuthEvent::class.java
        ) { listeners: Array<out ClientAuthEvent> ->
            ClientAuthEvent { session ->
                for (listener in listeners) {
                    listener.onAuth(session)
                }
            }
        }
    }
}