package org.qbrp.plasmo.model

class Region(val name: String) {
    val cuboids: MutableList<Cuboid> = mutableListOf(Cuboid.createGlobalCuboid())
    fun contains(x: Int, y: Int, z: Int): Boolean {
        return cuboids.all { it.contains(x, y, z) }
    }
}