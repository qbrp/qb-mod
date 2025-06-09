package org.qbrp.main.engine.items.components.physics

import kotlinx.serialization.Serializable
import org.qbrp.main.engine.items.components.containers.Dimensions
import org.qbrp.main.engine.items.components.ItemBehaviour

@Serializable
class Physics(val massGrams: Int, val dimensions: Dimensions): ItemBehaviour() {
    companion object {
        const val GRAVITY = 9.81
    }

    val substance: Substance get() = getComponent<Substance>()!!
    val densityGPerCm3: Double get() = substance.densityGPerCm3

    val weightGrams: Int
        get() = (massGrams * GRAVITY).toInt()
    val volume: Double
        get() = massGrams / densityGPerCm3
}