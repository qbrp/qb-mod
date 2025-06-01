package org.qbrp.main.engine.characters.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.serialization.Serializable

@Serializable
data class AppearanceData(val description: String, val looks: MutableList<Look>, val model: String) {
    val defaultLook: Look
        get() = looks[0]

    val look: Look
        get() = looks.find { it.name == appliedLookName } ?: defaultLook

    val appliedLookName: String = defaultLook.name

    @Serializable
    data class Look(val name: String, val skinUrl: String?, val description: String?) {
        fun getColoredName(character: CharacterData): String {
            return "<${character.colors[0]}>${name}"
        }
    }
}