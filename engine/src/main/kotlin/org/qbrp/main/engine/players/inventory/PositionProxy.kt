package org.qbrp.main.engine.players.inventory

import org.qbrp.main.core.components.StateUnavailableComponent
import org.qbrp.main.engine.world.location.Location
import org.qbrp.main.engine.world.location.WorldPos
import org.qbrp.main.engine.world.model.World

class PositionProxy(
    override val world: World,
    override val pos: WorldPos
) : StateUnavailableComponent, Location