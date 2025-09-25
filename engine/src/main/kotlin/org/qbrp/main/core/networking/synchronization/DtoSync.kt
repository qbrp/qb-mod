package org.qbrp.main.core.networking.synchronization

import org.qbrp.main.engine.players.model.ServerPlayerObject

interface DtoSync {
    fun componentToSync(player: ServerPlayerObject): Any = this
}