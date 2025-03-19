package org.qbrp.engine.client.engine.chat

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.world.World
import org.qbrp.core.visual.VisualDataStorage
import org.qbrp.engine.chat.ChatModule.Companion.SYSTEM_MESSAGE_AUTHOR
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.engine.chat.core.messages.ChatMessageTagsBuilder
import org.qbrp.engine.chat.core.system.ChatGroup
import org.qbrp.engine.chat.core.system.ChatGroupsStorage
import org.qbrp.engine.client.engine.chat.system.ChatTextTransformer
import org.qbrp.engine.client.engine.chat.system.MessageStorage
import org.qbrp.engine.client.engine.chat.system.Provider
import org.qbrp.engine.client.engine.chat.system.Typer
import org.qbrp.system.modules.ModuleAPI
import org.qbrp.system.networking.messages.Message
import org.qbrp.system.networking.messages.components.readonly.ClusterViewer

interface ClientChatAPI: ModuleAPI {
    fun isPlayerWriting(player: PlayerEntity): Boolean
    fun getWritingPlayers(world: World): List<PlayerEntity>
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