package org.qbrp.engine.client.engine.chat.system

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.hud.ChatHudLine
import net.minecraft.client.util.ChatMessages
import org.qbrp.engine.chat.messages.ChatMessage
import org.qbrp.engine.chat.messages.ChatMessageTagsBuilder
import org.qbrp.engine.chat.messages.ChatMessageTagsCluster
import org.qbrp.engine.client.engine.chat.system.events.ChatFormatEvent
import org.qbrp.system.networking.messages.components.Cluster
import org.qbrp.system.utils.format.Format.formatMinecraft

class MessageStorage {
    companion object MessageProvider {
        // Здесь вся логика, которая отвечает за отображение сообщений в HUD-е. Основной метод - calculateComputedMessages
        private val computedMessages: MutableList<ChatHudLine.Visible> = mutableListOf()
        private val messageHistory: MutableList<String> = mutableListOf()

        fun buildChatHudLines(content: String): List<ChatHudLine.Visible> {
            val text = content.formatMinecraft()
            val lines = ChatMessages.breakRenderedChatMessageLines(text, 700, MinecraftClient.getInstance().textRenderer);

            return lines.mapIndexed { index, orderedText ->
                ChatHudLine.Visible(
                    (MinecraftClient.getInstance().inGameHud?.ticks ?: 0).toInt(),
                    orderedText,
                    null,
                    index == lines.lastIndex
                )
            }.reversed()
        }
    }
    val messages: MutableList<ChatMessage> = emptyList<ChatMessage>().toMutableList()

    fun addMessage(messageString: String, author: String = "Debug", components: ChatMessageTagsBuilder = ChatMessageTagsBuilder()) {
        addMessage(ChatMessage(author, messageString, components))
    }

    fun addMessage(message: ChatMessage) {
        ChatFormatEvent.EVENT.invoker().handleMessage(message)
        messages.add(message)
        computedMessages.addAll(0,buildChatHudLines(message.getRawText()))
    }

    fun calculateComputedMessages(): List<ChatHudLine.Visible> {
        return computedMessages
    }

    fun provideMessageHistory(): List<String> {
        return messageHistory
    }

}