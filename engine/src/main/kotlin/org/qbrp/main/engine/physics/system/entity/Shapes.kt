package org.qbrp.main.engine.physics.system.entity

import org.qbrp.main.core.util.math.Vec3
import org.qbrp.main.engine.props.components.BoxDimensions

fun BoxDimensions.toShape(): Shape.Box {
    return Shape.Box(length / 2f.toDouble(), height / 2f.toDouble(), width / 2f.toDouble())
}

val Shape.Box.sizeVector get() = Vec3(xHalf * 2, yHalf * 2, zHalf * 2)

val Shape.Box.volume get() = 8 * xHalf * yHalf * zHalf

sealed class Shape {
    data class Box(val xHalf: Double, val yHalf: Double, val zHalf: Double) : Shape()
    data class SegmentCompound(val segments: List<Segment>) : Shape() {
        fun isEmpty() = segments.isEmpty() || !segments.any { !it.isEmpty() }
        fun subList(from: Int, to: Int): SegmentCompound {
            return SegmentCompound(segments.subList(from, to))
        }
    }
    data class Multi(val subshapes: List<SubShape>) : Shape()
    data class Plane(val y: Double) : Shape()
}

data class SubShape(val shape: Shape, val transform: Transform)

data class Segment(val y: Int, val flats: List<Flat>, val shapes: List<Shape.Box>) {
    fun isEmpty(): Boolean = flats.isEmpty() && shapes.isEmpty()
}

data class Flat(val startX: Int, val endX: Int, val startZ: Int, val endZ: Int)