package org.qbrp.main.core.mc.player.service.snapshot

import org.qbrp.main.core.mc.player.dataformats.MinecraftPlayerId
import org.qbrp.main.core.mc.player.model.EntitySnapshot
import org.qbrp.main.core.mc.player.model.PlayerAttributes
import org.qbrp.main.core.mc.player.model.ServerStateSnapshot
import org.qbrp.main.core.mc.player.model.impl.LazyMcServerPlayerSnapshot

interface MinecraftPlayerSnapshotService {
    fun lazySnapshotPlayer(id: MinecraftPlayerId): LazyMcServerPlayerSnapshot
    fun snapshotEntity(id: MinecraftPlayerId): EntitySnapshot
    fun snapshotServerState(id: MinecraftPlayerId): ServerStateSnapshot
    fun snapshotAttributes(id: MinecraftPlayerId): PlayerAttributes
}