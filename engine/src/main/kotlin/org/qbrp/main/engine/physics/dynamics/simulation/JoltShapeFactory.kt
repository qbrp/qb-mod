package org.qbrp.main.engine.physics.dynamics.simulation

import com.github.stephengold.joltjni.BoxShape
import com.github.stephengold.joltjni.CompoundShape
import com.github.stephengold.joltjni.Plane
import com.github.stephengold.joltjni.PlaneShape
import com.github.stephengold.joltjni.Quat
import com.github.stephengold.joltjni.ShapeResult
import com.github.stephengold.joltjni.StaticCompoundShapeSettings
import com.github.stephengold.joltjni.Vec3
import com.github.stephengold.joltjni.enumerate.EMotionType
import com.github.stephengold.joltjni.readonly.ConstShape
import org.qbrp.main.engine.physics.dynamics.chunk.factory.ChunkCollider
import org.qbrp.main.engine.physics.dynamics.dataformats.Shape
import org.qbrp.main.engine.physics.dynamics.dataformats.Vec3Adapter
import org.qbrp.main.engine.physics.dynamics.dataformats.jolt
import org.qbrp.main.engine.props.components.BoxDimensions
import org.qbrp.main.engine.physics.dynamics.shapes.BoxShape as EngineBoxShape
import kotlin.reflect.KClass

class JoltShapeFactory() {
    fun create(descriptor: Shape): ConstShape {
        return when (descriptor) {
            is ChunkCollider -> handleInterResult(chunkShape(descriptor), CompoundShape::class)
            is EngineBoxShape -> boxShape(descriptor)
            else -> throw NotImplementedError()
        }
    }

    private fun handleInterResult(result: ShapeResult, shapeClass: KClass<*>): ConstShape {
        if (result.hasError()) {
            throw IllegalStateException("Не удалось создать ${shapeClass.simpleName}: ${result.error}")
        } else {
            return result.get()
        }
    }

    private fun boxShape(dim: EngineBoxShape): BoxShape {
        return BoxShape(dim.xHalf.toFloat(), dim.yHalf.toFloat(), dim.zHalf.toFloat())
    }

    private fun chunkShape(collider: ChunkCollider): ShapeResult {
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
                val halfX = (box.xLength  / 2f).toFloat()
                val halfY = (box.yLength  / 2f).toFloat()
                val halfZ = (box.zLength  / 2f).toFloat()

                val center = Vec3Adapter(box.center).jolt()

                val boxShape = BoxShape(halfX, halfY, halfZ)
                settings.addShape(center, Quat.sIdentity(), boxShape)
            }
        }

        return settings.create()
    }
}