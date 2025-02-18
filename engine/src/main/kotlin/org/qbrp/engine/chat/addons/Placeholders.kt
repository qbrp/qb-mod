package org.qbrp.engine.chat.addons

import net.minecraft.util.ActionResult
import org.qbrp.engine.chat.core.events.MessageSendEvent
import org.qbrp.engine.chat.core.events.MessageUpdateEvent
import org.qbrp.engine.chat.core.messages.ChatMessage

class Placeholders {
    init {
        MessageUpdateEvent.EVENT.register { message ->
            updatePlaceholders(message) { key -> !key.contains(".onSend") }
            ActionResult.PASS
        }

        MessageSendEvent.EVENT.register { _, message, _, _ ->
            updatePlaceholders(message) { key -> key.contains(".onSend") }
            ActionResult.PASS
        }
    }

    private fun updatePlaceholders(
        message: ChatMessage,
        filter: (String) -> Boolean
    ) {
        val placeholders = message.getTags().getValueComponents("value")
        var updatedText = message.getText()
        placeholders.filterKeys(filter).forEach { (key, value) ->
            updatedText = updatedText.replace("{${key.split(".")[0]}}", value)
        }
        message.setText(updatedText)
    }
}