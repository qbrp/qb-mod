package core.players.service

import org.qbrp.main.core.mc.player.service.attribute.MinecraftPlayerAttributeServiceImpl
import org.qbrp.main.core.mc.player.dataformats.MinecraftPlayerId
import org.qbrp.main.core.mc.player.model.EntitySnapshot
import org.qbrp.main.core.mc.player.model.PlayerAttributes
import org.qbrp.main.core.mc.player.model.ServerStateSnapshot
import org.qbrp.main.core.mc.player.model.impl.LazyMcServerPlayerSnapshot
import org.qbrp.main.core.mc.player.service.MinecraftPlayerManager
import org.qbrp.main.core.mc.player.service.snapshot.AbstractSnapshotService

class PlayerSnapshotServiceMock(private val playerManager: MinecraftPlayerManager, attributesService: MinecraftPlayerAttributeServiceImpl): AbstractSnapshotService(attributesService) {
    override fun lazySnapshotPlayer(id: MinecraftPlayerId): LazyMcServerPlayerSnapshot {
        return lazySnapshotPlayer(playerManager.getById(id)!!)
    }

    override fun snapshotEntity(id: MinecraftPlayerId): EntitySnapshot {
        return snapshotEntity(playerManager.getById(id)!!)
    }

    override fun snapshotServerState(id: MinecraftPlayerId): ServerStateSnapshot {
        return snapshotServerState(playerManager.getById(id)!!)
    }

    override fun snapshotAttributes(id: MinecraftPlayerId): PlayerAttributes {
        return snapshotAttributes(playerManager.getById(id)!!)
    }
}