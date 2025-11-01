package org.qbrp.client.engine.players

import org.qbrp.main.engine.players.characters.entity.CharacterData

interface ClientCharactersBank {
    val character: CharacterData?

    fun applyCharacter(character: CharacterData)
}