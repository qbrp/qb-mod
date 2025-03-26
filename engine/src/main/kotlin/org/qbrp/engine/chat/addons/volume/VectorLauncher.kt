package org.qbrp.engine.chat.addons.volume

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import org.qbrp.system.utils.Tracer
import kotlin.math.*
import kotlinx.coroutines.*
import org.qbrp.core.ServerCore

class VectorLauncher {

    fun generateSphericalDirections(density: Int): List<Vec3d> {
        val directions = mutableListOf<Vec3d>()

        for (i in 0 until density) {
            val phi = Math.PI * (i + 1) / (density + 1)
            val y = cos(phi)
            val horizontalCount = (density * sin(phi)).roundToInt()
            for (j in 0 until horizontalCount) {
                val theta = 2 * Math.PI * j / horizontalCount
                val x = sin(phi) * cos(theta)
                val z = sin(phi) * sin(theta)
                directions.add(Vec3d(x, y, z))
            }
        }

        directions.add(Vec3d(0.0, 1.0, 0.0))
        directions.add(Vec3d(0.0, -1.0, 0.0))

        return directions
    }

    fun launchVectors3D(start: Vec3d, length: Double, density: Int, forward: Vec3d, modify: (BlockPos) -> Boolean) {
        val normalizedForward = forward.normalize()
        val directions = generateSphericalDirections(density)
        val filteredDirections = directions.filter { it.dotProduct(normalizedForward) > 0 }

        // Создаем скоуп для корутин
        runBlocking {
            filteredDirections.forEach { dir ->
                launch(Dispatchers.Default) {
                    val end = if (length > 0) {
                        start.add(dir.multiply(length))
                    } else {
                        start.add(dir.multiply(1000.0))
                    }
                    val tracerModify = if (length > 0) {
                        { pos: BlockPos -> modify(pos); true }
                    } else {
                        modify
                    }
                    // Переключаемся на основной поток для операций с блоками
                    ServerCore.server.execute {
                        Tracer.tracePathAndModify(start, end, tracerModify)
                    }
                }
            }
        }
    }
}