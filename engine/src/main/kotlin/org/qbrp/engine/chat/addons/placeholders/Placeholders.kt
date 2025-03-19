package org.qbrp.engine.chat.addons.placeholders

import net.minecraft.util.ActionResult
import org.qbrp.core.game.player.PlayerManager
import org.qbrp.engine.chat.ChatAddon
import org.qbrp.engine.chat.ChatModule.Companion.SYSTEM_MESSAGE_AUTHOR
import org.qbrp.engine.chat.core.events.MessageReceivedEvent
import org.qbrp.engine.chat.core.events.MessageSendEvent
import org.qbrp.engine.chat.core.events.MessageUpdateEvent
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.LoadPriority
import org.qbrp.system.modules.ModuleAPI

@Autoload(LoadPriority.ADDON, both = true)
class Placeholders: ChatAddon("placeholders"), PlaceholdersAPI {
    override fun handle(message: ChatMessage, filter: (String) -> Boolean) {
        val placeholders = message.getTags().getValueComponents("value")
        var updatedText = message.getText()
        placeholders.filterKeys(filter).forEach { (key, value) ->
            updatedText = updatedText.replace("{${key.split(".")[0]}}", value)
        }
        message.setText(updatedText, false)
    }

    override fun getAPI(): PlaceholdersAPI = this

    override fun load() {
        MessageReceivedEvent.EVENT.register { message ->
            val author = message.getAuthorEntity()
            val name = author?.name?.string ?: SYSTEM_MESSAGE_AUTHOR
            message.getTagsBuilder()
                .placeholder("playerName", name)
                .placeholder("playerDisplayName", author?.displayName?.string ?: SYSTEM_MESSAGE_AUTHOR)
                .placeholder("playerRpName", PlayerManager.getPlayerData(name)?.getDisplayName() ?: name)
            ActionResult.PASS
        }

        MessageUpdateEvent.EVENT.register { message ->
            handle(message) { key -> !key.contains(".onSend") }
            ActionResult.PASS
        }

        MessageSendEvent.EVENT.register { _, message, _, _ ->
            handle(message)
            ActionResult.PASS
        }
    }
}
