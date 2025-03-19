package org.qbrp.system.utils

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

object Tracer {

    fun tracePathAndModify(start: Vec3d, end: Vec3d, modify: (BlockPos) -> Unit) {
        val dx = end.x - start.x
        val dy = end.y - start.y
        val dz = end.z - start.z

        // Определяем количество шагов (по наибольшему изменению координаты)
        val steps = max(abs(dx), max(abs(dy), abs(dz))).roundToInt()
        val stepX = dx / steps
        val stepY = dy / steps
        val stepZ = dz / steps

        var currentX = start.x
        var currentY = start.y
        var currentZ = start.z

        for (i in 0..steps) {
            val currentBlock = BlockPos(currentX.roundToInt(), currentY.roundToInt(), currentZ.roundToInt())
            modify(currentBlock)

            currentX += stepX
            currentY += stepY
            currentZ += stepZ
        }
    }
}
