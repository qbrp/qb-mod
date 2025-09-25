package org.qbrp.main.core.components

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.util.ActionResult

fun interface ComponentInstantiationEvent {
    fun onInstantiate(state: ComponentManager, component: AnyComponent)

    companion object {
        val EVENT: Event<ComponentInstantiationEvent> = EventFactory.createArrayBacked(
            ComponentInstantiationEvent::class.java,
            { listeners: Array<out ComponentInstantiationEvent> ->
                ComponentInstantiationEvent { state, component ->
                    for (listener in listeners) {
                        listener.onInstantiate(state, component)
                    }
                    ActionResult.PASS
                }
            }
        )
    }
}