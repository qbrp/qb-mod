package org.qbrp.client.engine.chat.system

import config.ClientConfig
import net.minecraft.client.MinecraftClient
import org.qbrp.main.engine.chat.addons.tools.MessageTextTools
import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.client.engine.chat.system.logs.ChatData.MessageDTO
import org.qbrp.client.engine.chat.system.events.MessageAddedEvent
import org.qbrp.client.engine.chat.system.logs.ChatLogger
import org.qbrp.main.core.utils.format.Format.asMiniMessage
import org.qbrp.main.core.utils.log.LoggerUtil
import java.util.concurrent.CopyOnWriteArrayList

class MessageStorage(val chatLogger: ChatLogger) {
    private val messages: CopyOnWriteArrayList<ChatMessage> = CopyOnWriteArrayList()
    val logger = LoggerUtil.get("chat", "receiving")
    val messageHistory: MutableList<String> = mutableListOf()
    var provider: Provider = LinearMessageProvider()

    fun getMessages(from: Int = 0, to: Int = 200): List<ChatMessage> {
        if (messages.isEmpty()) return emptyList()
        val size = messages.size
        val safeTo = (size - from).coerceIn(0, size)
        val safeFrom = (size - to).coerceIn(0, safeTo)

        return messages.subList(safeFrom, safeTo)
    }

    fun getSize(): Int = messages.size

    fun clear(filter: (ChatMessage) -> Boolean = { true }) {
        messages.removeIf(filter)
        provider.onClear(this)
        addMessage("&aЧат очищен.")
    }

    fun addMessage(messageString: String, author: String = "Debug") {
        addMessage(ChatMessage(author, messageString))
    }

    private val messageLock = Any()

    fun addMessage(message: ChatMessage) {
        val client = MinecraftClient.getInstance()
        synchronized(messageLock) {
            messages.add(message)
            chatLogger.session.addMessage(getMessageDTO(message))
            chatLogger.saveSession()
            logger.log(
                "<<${message.authorName}>> --> ${message.getText()}" +
                        message.getTags().toList().joinToString("\n- ") { it.toString() }
            )
            MessageAddedEvent.EVENT.invoker().invokeEvent(message, this)
            if (message.getText().trim() != "") provider.onMessageAdded(message, this)
            if (message.getTags().getComponentData<Boolean>("bubble") == true && message.authorName == client.player?.name?.string) {
                client.player?.networkHandler?.sendCommand("/cb ${MessageTextTools.getTextContent(message)}")
            }
            if (getSize() > ClientConfig.chatSize) {
                val toRemove = messages.subList(0, 4).toList()
                toRemove.forEach {
                    provider.onMessageDeleted(it.uuid, this@MessageStorage)
                }
                messages.subList(0, 4).clear()
            }
        }
        if (message.handleVanilla() && message.authorName == client.player?.name?.string) {
            client.player?.networkHandler?.sendChatMessage(MessageTextTools.getTextContent(message).asMiniMessage().copy().string)
        }
    }

    fun editMessage(modifier: (Int, ChatMessage) -> ChatMessage?) {
        for (i in messages.indices) {
            val oldMessage = messages[i]
            val newMessage = modifier(i, oldMessage)
            if (newMessage != null) {
                messages[i] = newMessage
                provider.onMessageEdited(newMessage.uuid, newMessage, this)
            }
        }
    }

    fun getMessageDTO(message: ChatMessage): MessageDTO {
        return MessageDTO(message.getText(), message.authorName, message.uuid)
    }
}