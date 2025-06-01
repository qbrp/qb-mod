package org.qbrp.main.core.regions.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.serialization.Serializable
import org.qbrp.main.core.game.model.objects.BaseEntity
import org.qbrp.main.core.game.serialization.Identifiable

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