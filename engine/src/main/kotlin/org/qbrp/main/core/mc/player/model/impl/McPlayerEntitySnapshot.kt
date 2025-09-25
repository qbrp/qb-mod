package org.qbrp.main.core.mc.player.model.impl

import org.qbrp.main.core.mc.player.model.EntitySnapshot
import org.qbrp.main.engine.world.location.Position
import org.qbrp.main.engine.physics.dynamics.dataformats.Vec3Adapter

data class McPlayerEntitySnapshot(
    override val eyePos: Position,
    override val rotationVector: Vec3Adapter
): EntitySnapshot