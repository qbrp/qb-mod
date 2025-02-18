package org.qbrp.engine.chat.addons.tools

import net.minecraft.util.ActionResult
import org.qbrp.engine.chat.core.events.MessageUpdateEvent

class MessageTextEvents {
    init {
        MessageUpdateEvent.EVENT.register { message ->
            val textContent = message.getTags().getComponentData<String>("textContent") ?: return@register ActionResult.PASS
            message.apply {
                setTags(getTagsBuilder()
                    .placeholder("text.onSend", textContent))
            }
            ActionResult.PASS
        }
    }
}