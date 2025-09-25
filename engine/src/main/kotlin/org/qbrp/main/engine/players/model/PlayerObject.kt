package org.qbrp.main.engine.players.model

import org.qbrp.main.core.components.construct.CtxComponentBuilderOperator
import org.qbrp.main.core.components.objects.StateObject
import org.qbrp.main.core.mc.player.dataformats.MinecraftPlayerId
import org.qbrp.main.core.mc.player.dataformats.Username
import org.qbrp.main.engine.containers.v2.entity.ContainerEntity
import org.qbrp.main.engine.physics.AgentEntity
import org.qbrp.main.engine.players.model.components.PlayerComponentInstantiateContext
import org.qbrp.main.engine.players.model.components.PlayerState
import org.qbrp.main.engine.players.model.util.PlayerId

typealias PlayerComponentOperator = CtxComponentBuilderOperator<PlayerComponentInstantiateContext>

abstract class PlayerObject(
    open val username: Username,
    val minecraftPlayerId: MinecraftPlayerId,
    val playerId: PlayerId,
    override val state: PlayerState,
): StateObject<PlayerState>(state), PlayerComponentOperator, AgentEntity, ContainerEntity {
    override val id: String get() = playerId.value
    override val componentContext: PlayerComponentInstantiateContext get() = PlayerComponentInstantiateContext(this, state)
}
