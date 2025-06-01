package org.qbrp.client.engine.chat

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.world.World
import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.client.engine.chat.system.ChatTextTransformer
import org.qbrp.client.engine.chat.system.MessageStorage
import org.qbrp.client.engine.chat.system.Provider
import org.qbrp.client.engine.chat.system.Typer
import org.qbrp.main.core.modules.ModuleAPI
import org.qbrp.main.core.utils.networking.messages.Message

interface ClientChatAPI {
    fun isPlayerWriting(player: PlayerEntity): Boolean
    fun endTyping(player: PlayerEntity)
    fun startTyping(player: PlayerEntity)
    fun createMessageFromContext(context: Typer.TypingMessageContext): ChatMessage
    fun clearStorage()
    fun clearSystemMessages()
    fun sendMessageToServer(message: ChatMessage)
    fun getStorage(): MessageStorage
    fun addMessage(message: ChatMessage)
    fun handleMessageFromServer(message: Message)
    fun getTypingContextFromText(text: String): Typer.TypingMessageContext
    fun getMessageProvider(): Provider
    fun getTextTransformer(): ChatTextTransformer
}