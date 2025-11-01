package org.qbrp.main.core.regions

import kotlinx.serialization.Serializable
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import org.qbrp.main.core.components.serialization.Identifiable
import org.qbrp.main.core.util.format.Format.formatMm
import org.qbrp.main.engine.music.plasmo.view.View
import kotlin.math.sqrt

@Serializable
class Region(val name: String, val cuboids: List<Cuboid> = mutableListOf()): Identifiable {
    override val id: String get() = name

    fun getVolume(): Int = cuboids.sumOf { it.getVolume() }

    fun distanceTo(x: Double, y: Double, z: Double): Double {
        return cuboids.map { it.distanceToBoundary(x, y, z) }
            .minOrNull() ?: Double.MAX_VALUE
    }

    fun contains(x: Int, y: Int, z: Int): Boolean {
        return cuboids.all { it.contains(x, y, z) }
    }
}

class RegionView(val region: Region, val source: ServerPlayerEntity): View {
    private fun getHeader(): Text {
        return ("&a${region.name}&7 | Заполнение: &2${region.getVolume()}." +
                " &7Дистанция: &2${region.distanceTo(source.pos.x, source.pos.y, source.pos.z)}").formatMm()
    }

    override fun getText(): Text {
        return getHeader()
    }
}

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
