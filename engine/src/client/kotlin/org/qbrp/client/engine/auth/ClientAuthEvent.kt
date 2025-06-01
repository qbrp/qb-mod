package org.qbrp.client.engine.auth

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.client.network.ClientPlayNetworkHandler

fun interface ClientAuthEvent {
    fun onAuth(handler: ClientPlayNetworkHandler)

    companion object {
        val EVENT: Event<ClientAuthEvent> = EventFactory.createArrayBacked(
            ClientAuthEvent::class.java
        ) { listeners: Array<out ClientAuthEvent> ->
            ClientAuthEvent { handler ->
                for (listener in listeners) {
                    listener.onAuth(handler)
                }
            }
        }
    }
}