package org.qbrp.engine.characters.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class CharacterData(
    val name: String,
    val colors: List<String> = listOf("#FFFFFF", "#FFFFFF"),
    val bodyParts: List<String>,
    val appearance: AppearanceData,
    val sex: Sex,
    val height: Int,
    val id: Int
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