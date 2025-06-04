package org.qbrp.main.engine.synchronization.`interface`

import org.qbrp.main.core.mc.player.LocalPlayerObject

interface Synchronizable {
    fun shouldSync(player: LocalPlayerObject): Boolean
    fun trySync(player: LocalPlayerObject, synchronizer: Synchronizer) { if (shouldSync(player)) { synchronize(player, synchronizer) } }
    fun synchronize(playerObject: LocalPlayerObject, synchronizer: Synchronizer)
}