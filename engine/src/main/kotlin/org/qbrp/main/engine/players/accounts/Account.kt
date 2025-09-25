package org.qbrp.main.engine.players.accounts

import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.qbrp.main.core.components.serialization.Identifiable
import org.qbrp.main.mechanics.characters.model.CharacterData

@Serializable
class Account(@SerialName("id") val accountId: String,
              val playerId: String,
              @JsonSetter(nulls = Nulls.AS_EMPTY) val minecraftNicknames: MutableList<String> = mutableListOf(),
              val characters: List<CharacterData> = listOf(),
              var appliedCharacterName: String? = characters.getOrNull(0)?.name,
): Identifiable {
    override val id: String
        get() = accountId

    val appliedCharacter: CharacterData?
        get() = characters.find { it.name == appliedCharacterName } ?: characters.getOrNull(0)

    fun updateRegisteredNicknames(name: String) {
        if (!minecraftNicknames.contains(name)) { minecraftNicknames.add(name) }
    }
}