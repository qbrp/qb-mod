package org.qbrp.main.engine.physics.dynamics.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.util.ActionResult
import org.qbrp.main.engine.physics.dynamics.dataformats.Body
import org.qbrp.main.engine.physics.dynamics.simulation.WorldBodyManager
import org.qbrp.main.engine.players.characters.events.CharacterApplyEvent

object BodyEvents {
    fun interface BodyAddEvent {
        fun onBodyAdded(body: Body, manager: WorldBodyManager)
    }
    fun interface BodyRemovedEvent {
        fun onBodyRemoved(body: Body, manager: WorldBodyManager)
    }

    val ADDED: Event<BodyAddEvent> = EventFactory.createArrayBacked(
        BodyAddEvent::class.java
    ) { listeners: Array<out BodyAddEvent> ->
        BodyAddEvent { body, manager ->
            for (listener in listeners) {
                listener.onBodyAdded(body, manager)
            }
            ActionResult.PASS
        }
    }

    val REMOVED: Event<BodyRemovedEvent> = EventFactory.createArrayBacked(
        BodyRemovedEvent::class.java
    ) { listeners: Array<out BodyRemovedEvent> ->
        BodyRemovedEvent { body, manager ->
            for (listener in listeners) {
                listener.onBodyRemoved(body, manager)
            }
            ActionResult.PASS
        }
    }
}