package org.qbrp.core.regions.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class Region(val name: String, cuboids: List<Cuboid> = listOf()) {
    val cuboids: MutableList<Cuboid> = cuboids.toMutableList()

    @JsonIgnore
    fun getVolume(): Int = cuboids.sumOf { it.getVolume() }

    fun distanceTo(x: Double, y: Double, z: Double): Double {
        return cuboids.map { it.distanceToBoundary(x, y, z) }
            .minOrNull() ?: Double.MAX_VALUE
    }

    fun contains(x: Int, y: Int, z: Int): Boolean {
        return cuboids.all { it.contains(x, y, z) }
    }
}