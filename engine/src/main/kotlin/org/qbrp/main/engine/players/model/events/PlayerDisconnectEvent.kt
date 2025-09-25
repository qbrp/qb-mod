package org.qbrp.main.engine.players.model.events

import org.qbrp.main.engine.players.model.ServerPlayerObject

data class PlayerDisconnectEvent(val player: ServerPlayerObject): org.qbrp.main.core.events.Event