package org.qbrp.main.engine.players.auth.event

import org.qbrp.main.core.events.Event
import org.qbrp.main.core.mc.player.model.PlayerSnapshot
import org.qbrp.main.engine.players.accounts.Account
import org.qbrp.main.engine.players.auth.SessionData

data class SuccessLoginEvent(val player: PlayerSnapshot, val account: Account, val session: SessionData): Event