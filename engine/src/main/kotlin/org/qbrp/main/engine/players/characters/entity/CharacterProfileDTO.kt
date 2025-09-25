package org.qbrp.main.engine.players.characters.entity

import kotlinx.serialization.Serializable
import org.qbrp.main.core.components.state.StateJsonField
import org.qbrp.main.mechanics.characters.model.CharacterData

@Serializable
class CharacterProfileDTO(val id: Int, val data: CharacterData, val state: StateJsonField) {
    override fun equals(other: Any?): Boolean {
        return other is CharacterProfileDTO && id == other.id
    }

    override fun hashCode(): Int {
        return id
    }
}