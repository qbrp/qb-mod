package org.qbrp.main.mechanics.characters.model

import kotlinx.serialization.Serializable
import org.qbrp.main.engine.characters.model.AppearanceData
import java.util.UUID

@Serializable
data class CharacterData(
    val name: String,
    val colors: List<String> = listOf("#FFFFFF", "#FFFFFF"),
    val appearance: AppearanceData,
    val bioCategory: BioCategory = BioCategory.HUMAN,
    val height: Int = 170,
    val id: Int = UUID.randomUUID().hashCode(),
    val mass: Double = AVERAGE_MASS,
    val gripStrength: Double = AVERAGE_GRIP_STRENGTH, //ньютоны
) {
    companion object {
        const val AVERAGE_MASS = 70.0
        const val AVERAGE_GRIP_STRENGTH = 300.0
    }

    fun getTextWithColorTag(text: String): String {
        return "<gradient:${colors[0]}:${colors[1]}>$text</gradient>"
    }

    val formattedName: String
        get() = "<gradient:${colors[0]}:${colors[1]}>$name</gradient>"

    val scaleFactor: Double
        get() = 0.00531914894 * height

    val attributes: List<String> = emptyList()
}