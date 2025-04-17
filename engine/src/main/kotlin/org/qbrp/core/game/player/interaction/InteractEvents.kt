package org.qbrp.core.game.player.interaction

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.util.ActionResult

object InteractEvents {
    private val events = mutableMapOf<Pair<String, String>, Event<InteractionEvent>>()

    fun getOrCreate(mode: String, id: String): Event<InteractionEvent> {
        return events.getOrPut(Pair<String, String>(mode, id)) {
            EventFactory.createArrayBacked(InteractionEvent::class.java) { listeners ->
                InteractionEvent { player, intent ->
                    for (listener in listeners) {
                        listener.onInteraction(player, intent)
                    }
                    ActionResult.PASS
                }
            }
        }
    }
}
