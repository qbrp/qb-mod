package org.qbrp.client.render.hud

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import org.qbrp.client.engine.auth.ClientAuthEvent
import org.qbrp.client.render.inventory.InventoryWidget

fun interface InventoryHudEvents {
    fun onClose(widget: InventoryWidget)

    companion object {
        val CLOSE: Event<InventoryHudEvents> = EventFactory.createArrayBacked(
            InventoryHudEvents::class.java
        ) { listeners: Array<out InventoryHudEvents> ->
            InventoryHudEvents { widget ->
                for (listener in listeners) {
                    listener.onClose(widget)
                }
            }
        }
    }
}