package org.qbrp.main.engine.players.characters.entity

import org.qbrp.main.core.components.state.ComponentState
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.characters.components.CharacterComponentInstantiateContext
import org.qbrp.main.engine.players.characters.components.CharactersBank
import org.qbrp.main.mechanics.characters.model.CharacterData

typealias CharacterComponentBuilder = (CharacterComponentInstantiateContext, CharacterProfile) -> Unit

class CharacterFactory(val builders: MutableList<CharacterComponentBuilder> = mutableListOf()) {
    companion object {
        fun CharactersBank.upsertProfile(
            profile: CharacterProfile,
            prefab: CharacterFactory,
            ctx: CharacterComponentInstantiateContext
        ) {
            prefab.builders.forEach { it(ctx, profile) }
        }

        fun CharactersBank.createProfile(prefab: CharacterFactory, data: CharacterData): CharacterProfile {
            return prefab.createProfile(this.newCtx(data))
        }
    }

    fun createProfile(ctx: CharacterComponentInstantiateContext): CharacterProfile {
        val player = ctx.player as ServerPlayerObject
        val state = ComponentState<ServerPlayerObject>()
        state.parent = player
        val profile = CharacterProfile(player, ctx.character.id, ctx.character, state)
        builders.forEach { it(ctx, profile) }
        return profile
    }
}