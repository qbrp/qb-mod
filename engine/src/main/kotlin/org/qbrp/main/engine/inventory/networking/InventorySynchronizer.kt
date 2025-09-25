package org.qbrp.main.engine.inventory.networking

import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.players.model.ServerPlayerObject

interface InventorySynchronizer {
    fun broadcast(id: ContainerId)
    fun sync(id: ContainerId, player: ServerPlayerObject)
}