package org.qbrp.main.core.mc.player.model.impl

import org.qbrp.main.core.mc.player.dataformats.MinecraftPlayerId
import org.qbrp.main.core.mc.player.dataformats.Username
import org.qbrp.main.core.mc.player.model.EntitySnapshot
import org.qbrp.main.core.mc.player.model.PlayerAttributes
import org.qbrp.main.core.mc.player.model.PlayerSnapshot
import org.qbrp.main.core.mc.player.model.ServerStateSnapshot
import org.qbrp.main.core.mc.player.service.snapshot.MinecraftPlayerSnapshotService
import org.qbrp.main.engine.world.location.Position

class McPlayerStateView(override val username: Username,
                        override val uuid: MinecraftPlayerId,
                        private val snapshotService: MinecraftPlayerSnapshotService): PlayerSnapshot {
    override val pos: Position
        get() = snapshotLazy().pos
    override val entity: EntitySnapshot
        get() = snapshotService.snapshotEntity(uuid)
    override val serverState: ServerStateSnapshot
        get() = snapshotService.snapshotServerState(uuid)
    override val attributes: PlayerAttributes
        get() = snapshotService.snapshotAttributes(uuid)

    fun snapshotLazy(): LazyMcServerPlayerSnapshot {
        return snapshotService.lazySnapshotPlayer(uuid)
    }
}