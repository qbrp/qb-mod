package org.qbrp.core.mc.player

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import org.qbrp.engine.characters.model.CharacterData
import java.util.UUID

@JsonIgnoreProperties(ignoreUnknown = true)
class Account(@JsonSetter(nulls = Nulls.AS_EMPTY) val minecraftNicknames: MutableList<String> = mutableListOf(),
              val characters: List<CharacterData>,
              var appliedCharacterName: String? = characters.getOrNull(0)?.name,
              val uuid: String = UUID.randomUUID().toString()
) {
    @get:JsonIgnore
    val appliedCharacter: CharacterData?
        get() = characters.find { it.name == appliedCharacterName } ?: characters.getOrNull(0)

    fun updateRegisteredNicknames(name: String) {
        if (!minecraftNicknames.contains(name)) { minecraftNicknames.add(name) }
    }
}