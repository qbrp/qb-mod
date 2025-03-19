package org.qbrp.engine.chat.core.messages

import net.minecraft.util.ActionResult
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.qbrp.engine.Engine
import org.qbrp.engine.chat.ChatAPI
import org.qbrp.engine.chat.ChatModule.Companion.SYSTEM_MESSAGE_AUTHOR

class ChatException(val errorSource: ChatMessage, val reason: String): KoinComponent {
    val chatAPI = Engine.getAPI<ChatAPI>()

    fun send(
        sender: MessageSender = chatAPI!!.createSender()
        .apply { addTarget(errorSource.getAuthorEntity() ?: return@apply) }
    ): ActionResult {
        sender.send(
            ChatMessage(
                SYSTEM_MESSAGE_AUTHOR,
                "&c$reason")
        )
        return ActionResult.PASS
    }
}