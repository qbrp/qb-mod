package org.qbrp.main.engine.world.model

import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.engine.players.model.PlayerObject
import org.qbrp.main.engine.players.model.ServerPlayerObject

interface World : ComponentManager {
    val players: List<PlayerObject>
}