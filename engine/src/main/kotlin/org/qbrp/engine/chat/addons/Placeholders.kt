package org.qbrp.engine.chat.addons

import net.minecraft.util.ActionResult
import org.qbrp.engine.chat.core.events.MessageSendEvent

class Placeholders {
    init {
        MessageSendEvent.EVENT.register { sender, message ->
            val placeholders = message.getTags().getValueComponents("value")
            var updatedText = message.text

            placeholders.forEach { key, value ->
                updatedText = updatedText.replace("{$key}", value)
            }

            message.text = updatedText
            ActionResult.PASS
        }
    }
}