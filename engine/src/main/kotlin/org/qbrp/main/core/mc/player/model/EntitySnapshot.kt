package org.qbrp.main.core.mc.player.model

import org.qbrp.main.engine.world.location.Position
import org.qbrp.main.engine.physics.dynamics.dataformats.Vec3Adapter

interface EntitySnapshot {
    val eyePos: Position
    val rotationVector: Vec3Adapter
}