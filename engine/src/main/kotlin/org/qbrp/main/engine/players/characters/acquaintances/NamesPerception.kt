package org.qbrp.main.engine.players.characters.acquaintances

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.util.ActionResult
import org.qbrp.main.core.mc.player.ServerPlayerObject
import org.qbrp.main.core.mc.player.service.PlayerBehaviour
import org.qbrp.deprecated.resources.data.config.ConfigInitializationCallback
import org.qbrp.main.core.mc.player.PlayersUtil
import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.engine.chat.core.messages.MessageSender
import org.qbrp.main.engine.players.characters.Character
import org.qbrp.main.engine.players.characters.model.social.SocialKey
import org.qbrp.main.engine.players.nicknames.NicknamesModule

@Serializable
class NamesPerception: PlayerBehaviour() {
    companion object {
        var HIDE_PLAYER_NAMES_CHAT = true
        init {
            ConfigInitializationCallback.EVENT.register { config ->
                HIDE_PLAYER_NAMES_CHAT = config.chat.hidePlayerNameChat
            }
        }
    }

    @Serializable
    data class RenameEntry(
        val socialKey: SocialKey,
        val name: String
    )

    @SerialName("renames")
    private val renamesList: MutableList<RenameEntry> = mutableListOf()
    var ignore = false

    fun setName(character: Character, name: String) {
        val key = character.getSocialKey()
        // Обновляем или добавляем запись
        renamesList.removeAll { it.socialKey == key }
        renamesList.add(RenameEntry(key, name))
    }

    fun getName(character: Character): String {
        val rename = renamesList.find { it.socialKey == character.getSocialKey() }?.name
        return character.data.getTextWithColorTag(if (rename != null) (rename) else getUnknownName(character))
    }

    fun getUnknownName(character: Character): String {
        return character.data.bioCategory.displayName
    }

    fun getName(player: ServerPlayerObject): String {
        val ignore = player.getComponent<NamesPerception>()!!.ignore
        val displayName = player.getComponent<NicknamesModule.NicknameManager>()!!.getDisplayName()
        if (ignore) {
            return displayName
        } else {
            return getName(player.getComponent<Character>() ?: return displayName)
        }
    }

    override fun onChatMessage(sender: MessageSender, message: ChatMessage): ActionResult {
        val author = PlayersUtil.getPlayerSession(message.getAuthorEntity() ?: return ActionResult.PASS)
        val rename = getName(author)

        message.getTagsBuilder()
            .placeholder("playerRpName", rename)
        return ActionResult.PASS
    }
}
