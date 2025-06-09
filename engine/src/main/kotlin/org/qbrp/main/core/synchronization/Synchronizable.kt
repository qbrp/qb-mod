package org.qbrp.main.core.synchronization

import org.qbrp.main.core.mc.player.ServerPlayerObject

interface Synchronizable {
    fun shouldSync(player: ServerPlayerObject): Boolean
    fun trySync(player: ServerPlayerObject, synchronizer: Synchronizer) { if (shouldSync(player)) { synchronize(player, synchronizer) } }
    fun synchronize(playerObject: ServerPlayerObject, synchronizer: Synchronizer)
}