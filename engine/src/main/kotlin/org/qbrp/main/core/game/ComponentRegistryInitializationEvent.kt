package org.qbrp.main.core.game

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.util.ActionResult

fun interface ComponentRegistryInitializationEvent {
    fun onInitialized(registry: ComponentsRegistry)

    companion object {
        val EVENT: Event<ComponentRegistryInitializationEvent> = EventFactory.createArrayBacked(
            ComponentRegistryInitializationEvent::class.java,
            { listeners: Array<out ComponentRegistryInitializationEvent> ->
                ComponentRegistryInitializationEvent { registry ->
                    for (listener in listeners) {
                        listener.onInitialized(registry)
                    }
                    ActionResult.PASS
                }
            }
        )
    }
}