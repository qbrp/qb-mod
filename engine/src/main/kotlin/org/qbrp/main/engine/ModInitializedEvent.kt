package org.qbrp.main.engine

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory

fun interface ModInitializedEvent {
    fun event()

    companion object {
        val EVENT: Event<ModInitializedEvent> = EventFactory.createArrayBacked(
            ModInitializedEvent::class.java,
            { listeners: Array<out ModInitializedEvent> ->
                ModInitializedEvent {
                    for (listener in listeners) {
                        listener.event()
                    }
                }
            }
        )
    }
}