package org.qbrp.core.game.player

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import org.qbrp.engine.characters.model.Character
import org.qbrp.engine.characters.model.social.AccountSocial
import java.util.UUID

@JsonIgnoreProperties(ignoreUnknown = true)
class Account(@JsonSetter(nulls = Nulls.AS_EMPTY) val minecraftNicknames: MutableList<String> = mutableListOf(),
              val characters: List<Character>,
              var appliedCharacterName: String? = characters.getOrNull(0)?.name,
              val social: AccountSocial = AccountSocial(),
              val uuid: UUID = UUID.randomUUID()
) {
    @get:JsonIgnore
    val appliedCharacter: Character?
        get() = characters.find { it.name == appliedCharacterName } ?: characters.getOrNull(0)

    fun updateRegisteredNicknames(name: String) {
        if (!minecraftNicknames.contains(name)) { minecraftNicknames.add(name) }
    }
}