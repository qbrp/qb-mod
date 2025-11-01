package org.qbrp.main.core.regions

import net.minecraft.util.math.Vec3d

interface RegionsAPI {
    val regions: Collection<Region>
    fun nearest(position: Vec3d, count: Int = 1): List<Region>
    fun createRegion(name: String, cuboid: Cuboid)
    fun createRegion(name: String)
    fun removeRegion(name: String): Boolean
    fun getRegion(name: String): Region?
}