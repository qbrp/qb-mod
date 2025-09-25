package org.qbrp.main.engine.players.auth

import org.qbrp.main.engine.players.model.util.PlayerId
import java.util.UUID

data class SessionData(val token: UUID, val playerId: PlayerId) {}