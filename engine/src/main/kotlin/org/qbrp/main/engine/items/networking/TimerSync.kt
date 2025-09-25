package org.qbrp.main.engine.items.networking

import org.qbrp.main.core.networking.synchronization.SynchronizerComponent
import org.qbrp.main.engine.players.model.ServerPlayerObject

interface SyncListener {
    fun onSync(synchronizer: SynchronizerComponent<*>, player: ServerPlayerObject)
}