package org.qbrp.main.engine.physics.system.jolt

import com.github.stephengold.joltjni.BoxShape
import com.github.stephengold.joltjni.CompoundShape
import com.github.stephengold.joltjni.MutableCompoundShape
import com.github.stephengold.joltjni.MutableCompoundShapeSettings
import com.github.stephengold.joltjni.Plane
import com.github.stephengold.joltjni.PlaneShape
import com.github.stephengold.joltjni.Quat
import com.github.stephengold.joltjni.ShapeResult
import com.github.stephengold.joltjni.StaticCompoundShapeSettings
import com.github.stephengold.joltjni.Vec3
import com.github.stephengold.joltjni.readonly.ConstShape
import org.qbrp.main.core.util.math.asVec3
import org.qbrp.main.engine.physics.system.entity.Shape
import kotlin.reflect.KClass

fun createJoltShape(descriptor: Shape): ConstShape {
    return when (descriptor) {
        is Shape.Box -> boxShape(descriptor)
        is Shape.SegmentCompound -> segmentsCompoundShape(descriptor)
        is Shape.Plane -> PlaneShape(Plane(Vec3.sAxisY(), -descriptor.y.toFloat()))
        is Shape.Multi -> joltMutableMultiShape(descriptor)
    }
}

private fun handleInterResult(result: ShapeResult, shapeClass: KClass<*>): ConstShape {
    if (result.hasError()) {
        throw IllegalStateException("Не удалось создать ${shapeClass.simpleName}: ${result.error}")
    } else {
        return result.get()
    }
}

private fun boxShape(dim: Shape.Box): BoxShape {
    return BoxShape(dim.xHalf.toFloat(), dim.yHalf.toFloat(), dim.zHalf.toFloat())
}

internal fun joltMutableMultiShape(shape: Shape.Multi): MutableCompoundShape {
    val joltShape = MutableCompoundShape()
    shape.subshapes
        .map { createJoltShape(it.shape) to it.transform }
        .forEach { (shape, transform) -> joltShape.addShape(transform.pos.asVec3().jolt(), transform.rot.jolt(), shape) }
    return joltShape
}


private fun segmentsCompoundShape(collider: Shape.SegmentCompound): ConstShape {
    val settings = StaticCompoundShapeSettings()

    for (segment in collider.segments) {
        val yCenter = segment.y + 0.5f

        for (flat in segment.flats) {
            val x0 = flat.startX.toFloat()
            val z0 = flat.startZ.toFloat()

            val dx = flat.endX - flat.startX + 1
            val dz = flat.endZ - flat.startZ + 1

            val halfX = dx / 2f
            val halfZ = dz / 2f

            val center = Vec3(
                (x0 + halfX).toDouble(),
                yCenter.toDouble(),
                +   (z0 + halfZ).toDouble()
            )
            val boxShape = BoxShape(halfX, 0.5f, halfZ)
            settings.addShape(center, Quat.sIdentity(), boxShape)
        }

        for (box in segment.shapes) {
            val center = EngineVec3(box.xHalf, box.yHalf, box.zHalf).jolt()

            val boxShape = BoxShape(
                box.xHalf.toFloat(),
                box.yHalf.toFloat(),
                box.zHalf.toFloat()
            )
            settings.addShape(center, Quat.sIdentity(), boxShape)
        }
    }

    return handleInterResult(settings.create(), Shape.SegmentCompound::class)
}