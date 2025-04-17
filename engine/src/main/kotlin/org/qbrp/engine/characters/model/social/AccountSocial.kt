package org.qbrp.engine.characters.model.social

import org.qbrp.core.game.player.Account
import org.qbrp.engine.characters.model.Character

data class AccountSocial(
    val charactersSocial: MutableList<GlobalCharacterSocialData> = mutableListOf()
) {

    fun getCharacterData(accountId: String, characterId: Int): GlobalCharacterSocialData? {
        return charactersSocial.find { it.socialKey == SocialKey(accountId, characterId) }
    }

    fun getCharacterDataOrPut(accountId: String, characterId: Int): GlobalCharacterSocialData {
        return getCharacterData(accountId, characterId) ?: run {
            val newData = GlobalCharacterSocialData(SocialKey(accountId, characterId))
            charactersSocial.add(newData)
            newData
        }
    }

    fun isAppearanceRead(account: Account): Boolean {
        account.appliedCharacter?.let { character ->
            val data = getCharacterDataOrPut(account.uuid.toString(), character.id)
            val look = character.appearance.look
            return data.readDescriptionHash == character.appearance.description.hashCode() &&
                    (data.readLookHash == look.description.hashCode() || look.description == null)
        }
        return true
    }

    fun readAppearance(account: Account) {
        account.appliedCharacter?.let { character ->
            val data = getCharacterDataOrPut(account.uuid.toString(), character.id)
            data.readLookHash = character.appearance.look.description.hashCode()
            data.readDescriptionHash = character.appearance.description.hashCode()
        }
    }
}

