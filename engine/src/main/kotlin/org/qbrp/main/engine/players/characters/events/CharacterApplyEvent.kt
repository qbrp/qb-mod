package org.qbrp.main.engine.players.characters.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.util.ActionResult
import org.qbrp.main.mechanics.characters.model.CharacterData
import org.qbrp.main.engine.players.model.PlayerObject

data class CharacterApplyEvent(val player: PlayerObject, val character: CharacterData): org.qbrp.main.core.events.Event