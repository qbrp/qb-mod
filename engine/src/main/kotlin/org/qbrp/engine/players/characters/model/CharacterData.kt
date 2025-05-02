package org.qbrp.engine.characters.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.qbrp.engine.players.characters.model.BioCategory
import java.util.UUID

@JsonIgnoreProperties(ignoreUnknown = true)
data class CharacterData(
    val name: String,
    val colors: List<String> = listOf("#FFFFFF", "#FFFFFF"),
    val appearance: AppearanceData,
    val bioCategory: BioCategory,
    val height: Int,
    val id: Int = UUID.randomUUID().hashCode(),
) {
    @JsonIgnore
    fun getTextWithColorTag(text: String): String {
        return "<gradient:${colors[0]}:${colors[1]}>$text</gradient>"
    }

    @get:JsonIgnore
    val formattedName: String
        get() = "<gradient:${colors[0]}:${colors[1]}>$name</gradient>"

    @get:JsonIgnore
    val scaleFactor: Double
        get() = 0.00531914894 * height

    val attributes: List<String> = emptyList()
}