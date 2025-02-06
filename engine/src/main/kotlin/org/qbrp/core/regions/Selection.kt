package org.qbrp.core.regions

import net.minecraft.util.math.BlockPos

data class Selection(val firstPos: BlockPos? = null, val secondPos: BlockPos? = null) {
    fun isComplete(): Boolean = firstPos != null && secondPos != null

    fun convertToCuboid(): Cuboid? {
        if (!isComplete()) return null

        val first = firstPos!!
        val second = secondPos!!

        return Cuboid(
            minX = minOf(first.x, second.x),
            minY = minOf(first.y, second.y),
            minZ = minOf(first.z, second.z),
            maxX = maxOf(first.x, second.x),
            maxY = maxOf(first.y, second.y),
            maxZ = maxOf(first.z, second.z)
        )
    }
}
