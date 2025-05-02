package org.qbrp.engine.players.characters.acquaintances

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import net.minecraft.util.ActionResult
import org.qbrp.core.mc.player.PlayerManager
import org.qbrp.core.mc.player.model.PlayerBehaviour
import org.qbrp.core.resources.data.config.ConfigInitializationCallback
import org.qbrp.engine.characters.model.CharacterData
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.engine.chat.core.messages.MessageSender
import org.qbrp.engine.players.characters.Character
import org.qbrp.engine.players.characters.model.social.SocialKey

class NamesPerception: PlayerBehaviour() {
    companion object {
        var HIDE_PLAYER_NAMES_CHAT = true
        init {
            ConfigInitializationCallback.EVENT.register { config ->
                HIDE_PLAYER_NAMES_CHAT = config.chat.hidePlayerNameChat
            }
        }
    }

    data class RenameEntry(
        val socialKey: SocialKey,
        val name: String
    )

    @JsonProperty("renames")
    private val renamesList: MutableList<RenameEntry> = mutableListOf()

    @JsonIgnore
    fun setName(character: Character, name: String) {
        val key = character.getSocialKey()
        // Обновляем или добавляем запись
        renamesList.removeAll { it.socialKey == key }
        renamesList.add(RenameEntry(key, name))
    }

    @JsonIgnore
    fun getName(character: Character): String {
        val rename = renamesList.find { it.socialKey == character.getSocialKey() }?.name
        return if (rename != null) character.data.getTextWithColorTag(rename) else getUnknownName(character)
    }

    @JsonIgnore
    fun getUnknownName(character: Character): String {
        return character.data.bioCategory.displayName
    }

    override fun onMessage(sender: MessageSender, message: ChatMessage): ActionResult {
        val author = PlayerManager.getPlayerSession(message.getAuthorEntity() ?: return ActionResult.PASS)
        val character = author.state.getComponent<Character>() ?: return ActionResult.PASS
        val renameText = getName(character)
        val coloredName = character.data.getTextWithColorTag(renameText)

        message.getTagsBuilder()
            .placeholder("playerRpName", coloredName)
        return ActionResult.PASS
    }
}
