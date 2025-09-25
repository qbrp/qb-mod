package org.qbrp.main.engine.world.location

import org.qbrp.main.engine.world.model.World

interface Location {
    val world: World
    val pos: Position
}