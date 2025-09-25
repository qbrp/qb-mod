package org.qbrp.main.engine.physics.dynamics.dataformats

import org.qbrp.main.engine.world.location.Position

interface Transform {
    val pos: Position
    val quat: Quat
}