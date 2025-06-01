package org.qbrp.main.engine.chat.core.messages

import net.minecraft.util.ActionResult
import org.koin.core.component.KoinComponent
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.chat.ChatAPI
import org.qbrp.main.engine.chat.ChatModule.Companion.SYSTEM_MESSAGE_AUTHOR

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