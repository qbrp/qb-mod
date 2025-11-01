package org.qbrp.client.engine.players

import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.AbstractClientPlayerEntity
import org.qbrp.main.core.fabricmc.player.MinecraftPlayerId
import org.qbrp.main.core.util.PlayerId
import org.qbrp.main.engine.physics.player.MinecraftEntities

interface ClientMinecraftEntities : MinecraftEntities<AbstractClientPlayerEntity>

class ClientMinecraftEntityAccess(
    private val client: MinecraftClient,
    private val clientPlayerStorage: ClientPlayerStorage
) : ClientMinecraftEntities {
    override fun getEntity(playerId: PlayerId): AbstractClientPlayerEntity? {
        return clientPlayerStorage.getById(playerId.value)?.minecraftPlayerId?.let { getEntity(it) }
    }

    override fun getEntity(minecraftId: MinecraftPlayerId): AbstractClientPlayerEntity? {
        return client.world?.getPlayerByUuid(minecraftId.value) as AbstractClientPlayerEntity
    }
}