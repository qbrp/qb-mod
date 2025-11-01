package org.qbrp.main.engine.players.model

import org.qbrp.main.core.components.objects.StateObject
import org.qbrp.main.core.fabricmc.player.MinecraftPlayerId
import org.qbrp.main.core.fabricmc.player.PlayerReference
import org.qbrp.main.core.fabricmc.player.Username
import org.qbrp.main.engine.containers.entity.ContainerEntity
import org.qbrp.main.engine.chemistry.AgentEntity
import org.qbrp.main.core.util.PlayerId

abstract class PlayerObject(
    open val username: Username,
    val minecraftPlayerId: MinecraftPlayerId,
    val playerId: PlayerId,
    override val state: PlayerState,
) : StateObject<PlayerState>(state), AgentEntity, ContainerEntity {
    override val id: String get() = playerId.value
    val componentContext: PlayerComponentInstantiateContext
        get() = PlayerComponentInstantiateContext(this, state)
}
