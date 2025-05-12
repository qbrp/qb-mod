package org.qbrp.engine.client.core.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.client.network.ClientPlayNetworkHandler
import org.qbrp.engine.client.engine.contentpacks.ServerContentPackEvents
import org.qbrp.engine.client.engine.contentpacks.ServerPack

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