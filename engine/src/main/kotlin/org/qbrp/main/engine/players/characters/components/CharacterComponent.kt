package org.qbrp.main.engine.players.characters.components

import org.qbrp.main.core.components.Activateable
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.mechanics.characters.model.CharacterData
import org.qbrp.main.engine.players.model.components.PlayerComponent

open class CharacterComponent(ctx: CharacterComponentInstantiateContext, override var enabled: Boolean = false): PlayerComponent(ctx), Activateable, CharacterBehaviour {
    override val character: CharacterData by lazy { ctx.character }
    override val player: ServerPlayerObject by lazy { ctx.player as ServerPlayerObject }
}