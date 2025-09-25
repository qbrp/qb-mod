package org.qbrp.main.engine.players.characters.components

import org.qbrp.main.core.components.extensions.require
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.components.PlayerComponentBehaviour
import org.qbrp.main.engine.players.characters.components.CharactersBank
import org.qbrp.main.mechanics.characters.model.CharacterData

interface CharacterBehaviour: PlayerComponentBehaviour, CharacterBind {
    val character: CharacterData
    val bank get() = player.require<CharactersBank>()
    fun upsert(character: CharacterData) {}
}