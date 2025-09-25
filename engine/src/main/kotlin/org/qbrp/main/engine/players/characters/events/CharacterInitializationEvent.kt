package org.qbrp.main.engine.players.characters.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.util.ActionResult
import org.qbrp.main.mechanics.characters.model.CharacterData
import org.qbrp.main.engine.players.model.PlayerObject
import org.qbrp.main.engine.players.characters.components.CharactersBank

fun interface CharacterInitializationEvent {
    fun onCharacterInit(player: PlayerObject, bank: CharactersBank, character: CharacterData)

    companion object {
        val EVENT: Event<CharacterInitializationEvent> = EventFactory.createArrayBacked(
            CharacterInitializationEvent::class.java
        ) { listeners: Array<out CharacterInitializationEvent> ->
            CharacterInitializationEvent { player, bank, character ->
                for (listener in listeners) {
                   listener.onCharacterInit(player, bank, character)
                }
                ActionResult.PASS
            }
        }
    }
}