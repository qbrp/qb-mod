package org.qbrp.client.engine.players

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import org.qbrp.client.engine.players.profile.PlayerProfileManager
import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.network.player.ConnectionSession
import org.qbrp.main.core.util.PlayerId
import org.qbrp.main.engine.players.model.service.buildCommonComponents
import org.qbrp.main.engine.world.model.World

class ClientPlayerFactory(
    private val playerProfileManager: PlayerProfileManager,
    private val world: ClientWorld
) {
    fun buildLocalPlayer(id: PlayerId): ClientPlayerObject {
        val client = MinecraftClient.getInstance()
        return build(client.player!!, id)
    }

    fun build(entity: PlayerEntity, id: PlayerId): ClientPlayerObject {
        val profile = playerProfileManager.requireProfile(id)
        val player = ClientPlayerObject(id, profile.username, profile)
        player.state.parent = player
        player.buildCommonComponents(entity, world)
        return player
    }
}