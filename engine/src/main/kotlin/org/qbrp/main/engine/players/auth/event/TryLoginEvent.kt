package org.qbrp.main.engine.players.auth.event

import org.qbrp.main.core.events.Event
import org.qbrp.main.core.mc.player.model.PlayerSnapshot
import org.qbrp.main.engine.players.auth.LoginResult

data class TryLoginEvent(val player: PlayerSnapshot, val loginResult: LoginResult): Event