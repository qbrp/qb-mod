package org.qbrp.core.game.player.interaction.model

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import org.qbrp.core.game.player.interaction.Intent
import org.qbrp.engine.characters.model.Character

fun interface CharacterPrimaryInteraction {
    fun onInteract(character: Character, target: Character, intent: Intent)

    companion object {
        val EVENT: Event<CharacterPrimaryInteraction> =
            EventFactory.createArrayBacked(
                CharacterPrimaryInteraction::class.java
            ) { listeners ->
                CharacterPrimaryInteraction { character, target, intent ->
                    listeners.forEach {
                        it.onInteract(character, target, intent)
                    }
                }
        }
    }
}