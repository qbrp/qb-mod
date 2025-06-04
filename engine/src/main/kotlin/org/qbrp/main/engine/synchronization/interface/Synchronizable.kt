package org.qbrp.main.engine.synchronization.`interface`

import org.qbrp.main.core.mc.player.PlayerObject

interface Synchronizable {
    fun shouldSync(player: PlayerObject): Boolean
    fun trySync(player: PlayerObject, synchronizer: Synchronizer) { if (shouldSync(player)) { synchronize(player, synchronizer) } }
    fun synchronize(playerObject: PlayerObject, synchronizer: Synchronizer)
}