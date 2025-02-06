package org.qbrp.core.regions

data class Cuboid(val minX: Int, val minY: Int, val minZ: Int, val maxX: Int, val maxY: Int, val maxZ: Int) {
    fun contains(x: Int, y: Int, z: Int): Boolean {
        return x in minX..maxX && y in minY..maxY && z in minZ..maxZ
    }

    companion object {
        fun createGlobalCuboid(): Cuboid {
            return Cuboid(
                minX = -30_000_000,
                minY = -64,
                minZ = -30_000_000,
                maxX = 30_000_000,
                maxY = 319,
                maxZ = 30_000_000
            )
        }
    }
}
