package org.qbrp.engine.characters.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class AppearanceData(val description: String, val looks: MutableList<Look>, val model: String) {
    @get:JsonIgnore
    val defaultLook: Look
        get() = looks[0]

    @get:JsonIgnore
    val look: Look
        get() = looks.find { it.name == appliedLookName } ?: defaultLook

    val appliedLookName: String = defaultLook.name

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Look(val name: String, val skinUrl: String?, val description: String?) {
        fun getColoredName(character: CharacterData): String {
            return "<${character.colors[0]}>${name}"
        }
    }
}