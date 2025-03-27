package org.qbrp.engine.client.engine.chat.system

import net.minecraft.client.MinecraftClient
import org.qbrp.engine.chat.addons.tools.MessageTextTools
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.engine.client.core.resources.ClientResources
import org.qbrp.engine.client.core.resources.data.ChatData.MessageDTO
import org.qbrp.engine.client.engine.chat.system.events.MessageAddedEvent
import org.qbrp.system.utils.format.Format.asMiniMessage
import org.qbrp.system.utils.log.Loggers
import java.util.concurrent.CopyOnWriteArrayList

class MessageStorage {
    private val messages: CopyOnWriteArrayList<ChatMessage> = CopyOnWriteArrayList()
    val logger = Loggers.get("chat", "receiving")
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

    init {
        ClientResources.root.createChatLogSession()
    }

    fun clear(filter: (ChatMessage) -> Boolean = { true }) {
        ClientResources.root.chatLogs.save()
        messages.removeIf(filter)
        provider.clear()
        addMessage("&aЧат очищен.")
    }

    fun addMessage(messageString: String, author: String = "Debug") {
        addMessage(ChatMessage(author, messageString))
    }

    private val messageLock = Any()

    fun addMessage(message: ChatMessage) {
        synchronized(messageLock) {
            val client = MinecraftClient.getInstance()
            messages.add(message)
            ClientResources.root.addChatMessageToStorage(getMessageDTO(message))
            logger.log(
                "<<${message.authorName}>> --> ${message.getText()}" +
                        message.getTags().toList().joinToString("\n- ") { it.toString() }
            )
            MessageAddedEvent.EVENT.invoker().invokeEvent(message, this)
            if (message.getText().trim() != "") provider.onMessageAdded(message, this)
            if (message.handleVanilla() && message.authorName == client.player?.name?.string) {
                client.player?.networkHandler?.sendChatMessage(MessageTextTools.getTextContent(message))
            }
            if (message.getTags().getComponentData<Boolean>("bubble") == true && message.authorName == client.player?.name?.string) {
                client.player?.networkHandler?.sendCommand("/cb ${MessageTextTools.getTextContent(message)}")
            }
            if (getSize() > 300) {
                messages.subList(0, messages.size - 5).clear()
            }
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