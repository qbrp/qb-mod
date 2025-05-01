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

class ChatNickRename: PlayerBehaviour() {
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

    override fun onMessage(sender: MessageSender, message: ChatMessage): ActionResult {
        val author = PlayerManager.getPlayerSession(message.getAuthorEntity() ?: return ActionResult.PASS)
        val character = author.state.getComponent<Character>() ?: return ActionResult.PASS
        val key = character.getSocialKey()
        val entry = renamesList.find { it.socialKey == key }

        val renameText = entry?.name ?: if (HIDE_PLAYER_NAMES_CHAT) "???" else return ActionResult.PASS
        val coloredName = character.data.getTextWithColorTag(renameText)

        message.getTagsBuilder()
            .placeholder("playerRpName", coloredName)
        return ActionResult.PASS
    }
}
