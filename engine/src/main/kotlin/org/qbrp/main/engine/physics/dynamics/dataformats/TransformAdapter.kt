package org.qbrp.main.engine.physics.dynamics.dataformats

import org.qbrp.main.engine.world.location.Position

data class TransformAdapter(
    override val pos: Position,
    override val quat: Quat
): Transform {
}