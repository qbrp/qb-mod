package org.qbrp.main.engine.players.characters.entity

import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.characters.components.CharacterComponentInstantiateContext
import org.qbrp.main.mechanics.characters.model.CharacterData

class CharacterProfile(val player: ServerPlayerObject, val id: Int, val data: CharacterData, val state: ComponentManager): ComponentManager by state {
    val ctx get() = CharacterComponentInstantiateContext(player, data)

    override fun equals(other: Any?): Boolean {
        return other is CharacterProfile && id == other.id
    }

    override fun hashCode(): Int {
        return id
    }
}