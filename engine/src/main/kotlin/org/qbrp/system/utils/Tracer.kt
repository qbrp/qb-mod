package org.qbrp.system.utils
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt


object Tracer {

    fun tracePathAndModify(start: Vec3d, end: Vec3d, modify: (BlockPos) -> Unit) {
        val direction = end.subtract(start).normalize()
        var currentPos = start
        val maxDistance = start.distanceTo(end)
        var distanceTraveled = 0.0

        while (distanceTraveled <= maxDistance) {
            val currentBlock = BlockPos(currentPos.x.toInt(), currentPos.y.toInt(), currentPos.z.toInt())
            modify(currentBlock)

            // Двигаемся вдоль линии
            currentPos = currentPos.add(direction)
            distanceTraveled += direction.length()
        }
    }

}