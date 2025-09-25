package org.qbrp.main.engine.physics.dynamics.shapes

import org.qbrp.main.engine.props.components.BoxDimensions

fun BoxDimensions.toShape(): BoxShape {
    return BoxShapeImpl(length / 2f.toDouble(), height / 2f.toDouble(), width / 2f.toDouble())
}

data class BoxShapeImpl(
    override val xHalf: Double,
    override val yHalf: Double,
    override val zHalf: Double
): BoxShape