package org.qbrp.main.engine.characters.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.serialization.Serializable
import org.qbrp.main.engine.players.characters.model.BioCategory
import java.util.UUID

@Serializable
data class CharacterData(
    val name: String,
    val colors: List<String> = listOf("#FFFFFF", "#FFFFFF"),
    val appearance: AppearanceData,
    val bioCategory: BioCategory,
    val height: Int,
    val id: Int = UUID.randomUUID().hashCode(),
) {

    fun getTextWithColorTag(text: String): String {
        return "<gradient:${colors[0]}:${colors[1]}>$text</gradient>"
    }

    val formattedName: String
        get() = "<gradient:${colors[0]}:${colors[1]}>$name</gradient>"

    val scaleFactor: Double
        get() = 0.00531914894 * height

    val attributes: List<String> = emptyList()
}