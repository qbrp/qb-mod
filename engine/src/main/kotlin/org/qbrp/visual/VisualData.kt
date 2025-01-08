package org.qbrp.visual

import net.minecraft.world.World

abstract class VisualData(open val x: Int, open val y: Int, open val z: Int) {
    abstract val clazz: Class<out VisualData>
    abstract fun toJson(): String
    abstract fun fromJson(json: String, world: World): VisualData
}