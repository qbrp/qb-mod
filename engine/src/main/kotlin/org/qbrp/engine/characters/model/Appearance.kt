package org.qbrp.engine.characters.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Appearance(val description: String, val looks: MutableList<Look>, val model: String) {
    @get:JsonIgnore
    val defaultLook: Look
        get() = looks[0]

    @get:JsonIgnore
    val look: Look
        get() = appliedLook ?: defaultLook

    @get:JsonIgnore
    val skinUrl: String
        get() = appliedLook?.skinUrl ?: defaultLook.skinUrl!!

    var appliedLook: Look? = null

    fun composeDescription(look: Look = this.look): String {
        return "${description}${look.textDescription}"
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Look(val name: String, val skinUrl: String?, val description: String?) {
        @get:JsonIgnore
        val textDescription: String
            get() = if(description != null) "\n" + description else ""

        fun getColoredName(character: Character): String {
            return "<${character.colors[0]}>${name}"
        }
    }
}