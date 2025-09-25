package org.qbrp.main.engine.world.location

import org.qbrp.main.engine.world.location.Position

data class WorldPosition(override val x: Double, override val y: Double, override val z: Double) : Position {
    constructor() : this(0.0, 0.0, 0.0)
}