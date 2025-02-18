package org.qbrp.engine.chat.core.messages

import net.minecraft.util.ActionResult
import org.qbrp.engine.Engine
import org.qbrp.engine.chat.ChatModule.Companion.MESSAGE_AUTHOR_SYSTEM

class ChatException(val errorSource: ChatMessage, val reason: String) {
    fun send(
        sender: MessageSender = Engine.chatModule.API.createSender()
        .apply { addTarget(errorSource.getAuthorEntity() ?: return@apply) }
    ): ActionResult {
        sender.send(
            ChatMessage(
                MESSAGE_AUTHOR_SYSTEM,
                "&c$reason",
                ChatMessageTagsBuilder())
        )
        return ActionResult.PASS
    }
}