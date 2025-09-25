package org.qbrp.main.engine.items.event

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.util.ActionResult
import org.qbrp.main.engine.items.model.ServerItemObject

fun interface ItemLoadEvent {
    fun onItemLoad(item: ServerItemObject)

    companion object {
        val EVENT: Event<ItemLoadEvent> = EventFactory.createArrayBacked(
            ItemLoadEvent::class.java
        ) { listeners: Array<out ItemLoadEvent> ->
            ItemLoadEvent { item ->
                for (listener in listeners) {
                    listener.onItemLoad(item)
                }
                ActionResult.PASS
            }
        }
    }
}