package org.qbrp.system.utils

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

object Tracer {

    fun tracePathAndModify(start: Vec3d, end: Vec3d, modify: (BlockPos) -> Boolean) {
        val direction = end.subtract(start)
        val length = direction.length()
        if (length == 0.0) return

        val normalized = direction.normalize()
        val step = 0.25  // можно менять точность
        var traveled = 0.0
        var previousBlock: BlockPos? = null

        while (traveled <= length) {
            val pos = start.add(normalized.multiply(traveled))
            val blockPos = BlockPos(Vec3i(pos.x.toInt(), pos.y.toInt(), pos.z.toInt()))

            if (blockPos != previousBlock) {
                if (!modify(blockPos)) {
                    break
                }
                previousBlock = blockPos
            }

            traveled += step
        }
    }

}