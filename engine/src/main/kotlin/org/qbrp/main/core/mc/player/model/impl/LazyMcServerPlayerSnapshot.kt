package org.qbrp.main.core.mc.player.model.impl

import org.qbrp.main.core.mc.player.dataformats.MinecraftPlayerId
import org.qbrp.main.core.mc.player.dataformats.Username
import org.qbrp.main.core.mc.player.model.EntitySnapshot
import org.qbrp.main.core.mc.player.model.PlayerAttributes
import org.qbrp.main.core.mc.player.model.PlayerSnapshot
import org.qbrp.main.core.mc.player.model.ServerStateSnapshot
import org.qbrp.main.core.mc.player.service.snapshot.MinecraftPlayerSnapshotService
import org.qbrp.main.engine.world.location.Position

data class LazyMcServerPlayerSnapshot(
    private val factory: MinecraftPlayerSnapshotService,
    override val username: Username,
    override val pos: Position,
    override val uuid: MinecraftPlayerId,
) : PlayerSnapshot {
    override val entity: EntitySnapshot by lazy { factory.snapshotEntity(uuid) }
    override val serverState: ServerStateSnapshot by lazy { factory.snapshotServerState(uuid) }
    override val attributes: PlayerAttributes by lazy { factory.snapshotAttributes(uuid) }
}