package org.qbrp.main.system

import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.fabricmc.player.MinecraftPlayerId
import org.qbrp.main.engine.physics.player.MinecraftEntities
import org.qbrp.main.core.util.PlayerId
import org.qbrp.main.engine.physics.player.ServerMinecraftEntities
import org.qbrp.main.engine.players.model.service.ServerPlayerStorage

class ServerMinecraftEntitiesAccess(
    private val playerStorage: ServerPlayerStorage,
    private val server: MinecraftServer
) : ServerMinecraftEntities {
    override fun getEntity(playerId: PlayerId): ServerPlayerEntity? {
        val mcId = playerStorage.getById(playerId.value)?.minecraftPlayerId
        return mcId?.let { getEntity(it) }
    }

    override fun getEntity(minecraftId: MinecraftPlayerId): ServerPlayerEntity? {
        return server.playerManager.getPlayer(minecraftId.value)
    }
}