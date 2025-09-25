package org.qbrp.main.engine.physics.dynamics.dataformats

import kotlinx.serialization.Serializable
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.Vec3d
import org.qbrp.main.engine.world.location.Position

@Serializable
data class Vec3Adapter(override val x: Double, override val y: Double, override val z: Double): Position {
    constructor(mcVec: Vec3d): this(mcVec.x, mcVec.y, mcVec.z)
    constructor(x: Int, y: Int, z: Int): this(x.toDouble(), y.toDouble(), z.toDouble())
    constructor() : this(0.0, 0.0, 0.0)
    constructor(position: Position): this(position.x, position.z, position.y)
}