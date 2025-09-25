package org.qbrp.main.engine.players.model.exception

import org.qbrp.main.core.mc.player.dataformats.Username
import org.qbrp.main.engine.players.model.util.PlayerId

class PlayerAlreadyOnServerException(val playerId: PlayerId, val username: Username): RuntimeException("Игрок $username ($playerId) уже на сервере")