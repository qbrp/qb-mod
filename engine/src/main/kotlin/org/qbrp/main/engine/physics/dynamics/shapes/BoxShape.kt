package org.qbrp.main.engine.physics.dynamics.shapes

import org.qbrp.main.engine.physics.dynamics.dataformats.Vec3Adapter
import org.qbrp.main.engine.physics.model.VolumetricShape

interface BoxShape: VolumetricShape {
    val xHalf: Double
    val yHalf: Double
    val zHalf: Double

    override val volume: Double get() = 8 * xHalf * yHalf * zHalf
    fun sizeVector(): Vec3Adapter = Vec3Adapter(xHalf * 2, yHalf * 2, zHalf * 2)
}