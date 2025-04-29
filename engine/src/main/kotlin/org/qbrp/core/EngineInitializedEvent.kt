package org.qbrp.core

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory

fun interface EngineInitializedEvent {
    fun event()

    companion object {
        val EVENT: Event<EngineInitializedEvent> = EventFactory.createArrayBacked(
            EngineInitializedEvent::class.java,
            { listeners: Array<out EngineInitializedEvent> ->
                EngineInitializedEvent {
                    for (listener in listeners) {
                        listener.event()
                    }
                }
            }
        )
    }
}