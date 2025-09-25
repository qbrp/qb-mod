package org.qbrp.main.engine.players.model.events

import org.qbrp.main.core.events.Event
import org.qbrp.main.engine.players.model.ServerPlayerObject

data class PlayerInitBeforeJsonLoadEvent(val player: ServerPlayerObject): Event
