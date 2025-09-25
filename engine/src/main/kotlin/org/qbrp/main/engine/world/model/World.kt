package org.qbrp.main.engine.world.model

import org.qbrp.main.engine.players.model.ServerPlayerObject

interface World {
    val players: List<ServerPlayerObject>
}