package org.qbrp.main.engine.physics.dynamics.dataformats

import com.github.stephengold.joltjni.Quat
import kotlinx.serialization.Serializable

@Serializable
class QuatAdapter(
    override val x: Double,
    override val y: Double,
    override val z: Double,
    override val w: Double
) : org.qbrp.main.engine.physics.dynamics.dataformats.Quat {
    constructor(): this(0.0, 0.0, 0.0, 0.0)
    constructor(quat: Quat): this(quat.x.toDouble(), quat.y.toDouble(), quat.z.toDouble(), quat.w.toDouble())
}