package org.qbrp.main.engine.players.model

import org.qbrp.main.core.components.DataComponent
import org.qbrp.main.engine.players.model.components.PlayerComponent
import org.qbrp.main.engine.players.model.components.PlayerComponentInstantiateContext
import org.qbrp.main.engine.world.location.Location
import org.qbrp.main.engine.world.location.Position
import org.qbrp.main.engine.world.model.World

data class PlayerLocation(
    val ctx: PlayerComponentInstantiateContext,
) : PlayerComponent(ctx), Location {
    override val world: World get() = serverPlayer.context.world
    override val pos: Position get() = serverPlayer.pos
}