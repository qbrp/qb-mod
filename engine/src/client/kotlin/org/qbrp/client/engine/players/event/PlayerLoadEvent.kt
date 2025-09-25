package org.qbrp.client.engine.players.event

import org.qbrp.client.engine.players.ClientPlayerObject
import org.qbrp.main.core.events.Event

data class PlayerLoadEvent(val player: ClientPlayerObject): Event