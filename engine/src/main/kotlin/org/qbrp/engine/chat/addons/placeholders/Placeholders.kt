package org.qbrp.engine.chat.addons.placeholders

import net.minecraft.util.ActionResult
import org.qbrp.core.mc.player.PlayerManager
import org.qbrp.engine.chat.ChatModule.Companion.SYSTEM_MESSAGE_AUTHOR
import org.qbrp.engine.chat.core.events.MessageReceivedEvent
import org.qbrp.engine.chat.core.events.MessageSendEvent
import org.qbrp.engine.chat.core.events.MessageUpdateEvent
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.LoadPriority
import org.qbrp.system.modules.QbModule

@Autoload(LoadPriority.ADDON, both = true)
class Placeholders: QbModule("placeholders"), PlaceholdersAPI {
    override fun handle(message: ChatMessage, filter: (String) -> Boolean) {
        val placeholders = message.getTags().getValueComponents("value")
        var updatedText = message.getText()
        placeholders.filterKeys(filter).forEach { (key, value) ->
            updatedText = updatedText.replace("{${key}}", value)
        }
        message.setText(updatedText, false)
    }

    override fun getAPI(): PlaceholdersAPI = this

    override fun onLoad() {
        MessageReceivedEvent.EVENT.register { message ->
            val author = message.getAuthorEntity()
            val name = author?.name?.string ?: SYSTEM_MESSAGE_AUTHOR
            message.getTagsBuilder()
                .placeholder("playerName", name)
                .placeholder("playerDisplayName", author?.displayName?.string ?: SYSTEM_MESSAGE_AUTHOR)
                .placeholder("playerRpName", PlayerManager.getPlayerSession(name)?.displayName!! )
            ActionResult.PASS
        }

        MessageUpdateEvent.EVENT.register { message ->
            handle(message) { key -> !key.contains(".onSend") }
            ActionResult.PASS
        }

        MessageSendEvent.register( { _, message, receiver, _ ->
            handle(message)
            ActionResult.PASS
        }, MessageSendEvent.Companion.Priority.LAST)
    }
}
