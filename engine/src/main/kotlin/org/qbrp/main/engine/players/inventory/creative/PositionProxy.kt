package org.qbrp.main.engine.players.inventory.creative

import net.minecraft.server.world.ServerWorld
import org.qbrp.main.core.components.StateUnavailableComponent
import org.qbrp.main.engine.world.location.Position
import org.qbrp.main.engine.world.location.Location
import org.qbrp.main.engine.world.model.World

class PositionProxy(
    override val world: World,
    override val pos: Position
) : StateUnavailableComponent, Location