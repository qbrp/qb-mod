package org.qbrp.engine.characters.model

import com.fasterxml.jackson.annotation.JsonIgnore

data class Character(
    val name: String,
    val colors: List<String> = listOf("#FFFFFF", "#FFFFFF"),
    val bodyParts: List<String>,
    val appearance: Appearance,
    val sex: Sex,
    val height: Int) {

    @get:JsonIgnore
    val formattedName: String
        get() = "<gradient:${colors[0]}:${colors[1]}>$name</gradient>"

    @get:JsonIgnore
    val scaleFactor: Double
        get() = 0.00535 * height

    val knownNames: Map<String, String> = emptyMap()
    val attributes: List<String> = emptyList()
}