package org.qbrp.main.engine.players.characters

import org.qbrp.main.core.events.Event
import org.qbrp.main.engine.players.characters.entity.CharacterData
import org.qbrp.main.engine.players.model.PlayerObject

data class CharacterApplyEvent(val player: PlayerObject, val character: CharacterData): Event