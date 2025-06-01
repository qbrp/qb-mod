package org.qbrp.main.core.regions.model

import com.fasterxml.jackson.annotation.JsonIgnore
import kotlinx.serialization.Serializable
import kotlin.math.sqrt

@Serializable
data class Cuboid(val minX: Int, val minY: Int, val minZ: Int, val maxX: Int, val maxY: Int, val maxZ: Int) {

    fun getXBounds(): Pair<Int, Int> = Pair(minX, maxX)
    fun getYBounds(): Pair<Int, Int> = Pair(minY, maxY)
    fun getZBounds(): Pair<Int, Int> = Pair(minZ, maxZ)
    fun getXLength(): Int = maxX - minX
    fun getYLength(): Int = maxY - minY
    fun getZLength(): Int = maxZ - minZ

    fun distanceToBoundary(x: Double, y: Double, z: Double): Double {
        val (minX, maxX) = getXBounds()
        val (minY, maxY) = getYBounds()
        val (minZ, maxZ) = getZBounds()

        val distanceX = if (x < minX) minX - x else if (x > maxX) x - maxX else 0.0
        val distanceY = if (y < minY) minY - y else if (y > maxY) y - maxY else 0.0
        val distanceZ = if (z < minZ) minZ - z else if (z > maxZ) z - maxZ else 0.0

        return sqrt(distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ)
    }

    fun getVolume() = getXLength() * getYLength() * getZLength()

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
