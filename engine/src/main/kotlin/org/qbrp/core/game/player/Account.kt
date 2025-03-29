package org.qbrp.core.game.player

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import java.util.UUID

@JsonIgnoreProperties(ignoreUnknown = true)
class Account(@JsonSetter(nulls = Nulls.AS_EMPTY) val minecraftNicknames: MutableList<String>,
              var displayName: String = minecraftNicknames.first(),
              val uuid: UUID = UUID.randomUUID()
) {
    fun updateDisplayName(newName: String) { displayName = newName }

    fun updateRegisteredNicknames(name: String) {
        if (!minecraftNicknames.contains(name)) { minecraftNicknames.add(name) }
    }

    companion object {
        fun new(player: ServerPlayerSession) = Account(mutableListOf(player.entity.name.string))
    }
}