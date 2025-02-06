package org.qbrp.core.regions

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class Region(val name: String, cuboids: List<Cuboid> = listOf()) {
    val cuboids: MutableList<Cuboid> = cuboids.toMutableList()
    fun contains(x: Int, y: Int, z: Int): Boolean {
        return cuboids.all { it.contains(x, y, z) }
    }
}