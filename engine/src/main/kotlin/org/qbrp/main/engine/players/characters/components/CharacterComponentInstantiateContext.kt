package org.qbrp.main.engine.players.characters.components

import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.mechanics.characters.model.CharacterData
import org.qbrp.main.engine.players.model.components.PlayerComponentInstantiateContext

class CharacterComponentInstantiateContext(player: ServerPlayerObject,
                                           val character: CharacterData): PlayerComponentInstantiateContext(player, player.state)