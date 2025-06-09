package org.qbrp.main.engine.items.components.physics

import kotlinx.serialization.Serializable
import org.qbrp.main.engine.items.components.ItemBehaviour

@Serializable
class Material(
    val material: String,
    override val densityGPerCm3: Double = MATERIALS.getValue(material).densityGPerCm3
) : ItemBehaviour(), Substance {
    companion object {
        val MATERIALS = mapOf(
            "METAL" to Material("METAL", 8.0)
        )
    }
}
