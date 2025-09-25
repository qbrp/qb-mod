package org.qbrp.main.core.mc.player.service.snapshot

import org.qbrp.main.core.mc.player.dataformats.MinecraftPlayerId
import org.qbrp.main.core.mc.player.model.EntitySnapshot
import org.qbrp.main.core.mc.player.model.ServerStateSnapshot
import org.qbrp.main.core.mc.player.model.impl.LazyMcServerPlayerSnapshot
import org.qbrp.main.core.mc.player.service.attribute.MinecraftPlayerAttributeService
import org.qbrp.main.core.mc.player.getEntity
import org.qbrp.main.core.mc.player.model.PlayerAttributes
import org.qbrp.main.core.mc.player.service.MinecraftPlayerManager
import org.qbrp.main.core.mc.server.thread.ServerThread

class ServerPlayerSnapshotService(
    private val playerManager: MinecraftPlayerManager,
    private val serverThread: ServerThread,
    attributesService: MinecraftPlayerAttributeService
) : AbstractSnapshotService(attributesService) {
    override fun lazySnapshotPlayer(uuid: MinecraftPlayerId): LazyMcServerPlayerSnapshot {
        return lazySnapshotPlayer(uuid.getEntity(playerManager))
    }

    override fun snapshotEntity(id: MinecraftPlayerId): EntitySnapshot {
        return snapshotEntity(id.getEntity(playerManager))
    }

    override fun snapshotServerState(id: MinecraftPlayerId): ServerStateSnapshot {
        return snapshotServerState(id.getEntity(playerManager))
    }

    override fun snapshotAttributes(id: MinecraftPlayerId): PlayerAttributes {
        return snapshotAttributes(id.getEntity(playerManager))
    }
}