package org.qbrp.engine.chat.messages

import net.minecraft.util.ActionResult
import org.qbrp.engine.Engine
import org.qbrp.engine.chat.ChatModule.Companion.MESSAGE_AUTHOR_SYSTEM
import org.qbrp.system.networking.messages.components.ClusterBuilder
import org.qbrp.system.networking.messages.types.StringContent

class ChatException(val errorSource: ChatMessage, val reason: String) {
    fun send(
        sender: MessageSender = Engine.chatModule.API.createSender()
        .apply { addTarget(errorSource.getAuthorEntity()!!) }
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