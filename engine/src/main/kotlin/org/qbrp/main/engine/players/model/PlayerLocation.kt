package org.qbrp.main.engine.players.model

import org.qbrp.main.core.components.DataComponent
import org.qbrp.main.core.components.extensions.require
import org.qbrp.main.engine.world.location.Location
import org.qbrp.main.engine.world.location.WorldPos
import org.qbrp.main.engine.world.model.World

data class PlayerLocation(
    override val world: World,
    override var pos: WorldPos
) : DataComponent, Location

val PlayerObject.location get() = this.require<PlayerLocation>()

val PlayerObject.pos get() = this.location.pos

val PlayerObject.world get() = this.location.world