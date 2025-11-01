package org.qbrp.client.engine.chat

import net.fabricmc.api.EnvType
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import org.qbrp.main.engine.chat.ChatModule.Companion.SYSTEM_MESSAGE_AUTHOR
import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.engine.chat.core.messages.ChatMessageTagsBuilder
import org.qbrp.client.engine.chat.system.ChatTextTransformer
import org.qbrp.client.engine.chat.system.ClientChatNetworking
import org.qbrp.client.engine.chat.system.MessageStorage
import org.qbrp.client.engine.chat.system.Typer
import org.qbrp.client.engine.chat.system.logs.ChatLogger
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.util.networking.messages.Message

@Autoload(env = EnvType.CLIENT)
class ChatModuleClient(): EngineModule("chat-client"), ClientChatAPI {
    lateinit var messageStorage: MessageStorage
    lateinit var networking: ClientChatNetworking

    init {
        createModuleFileOnInit()
    }

    override fun getKoinModule() = inner<ClientChatAPI>(this) {
        scoped { ChatLogger(getModuleFile(), get()) }
        scoped { MessageStorage(get()) }
        scoped { ClientChatNetworking(get()) }
        scoped { ChatTextTransformer() }
        scoped { Typer() }
    }

    override fun getAPI(): ClientChatAPI = this

    override fun onLoad() {
        messageStorage = getLocal<MessageStorage>()
        networking = getLocal<ClientChatNetworking>().apply {
            registerReceivers()
        }
    }

    override fun isPlayerWriting(player: PlayerEntity): Boolean = TODO()

    override fun endTyping(player: PlayerEntity) { networking.sendEndTypingStatus() }
    override fun startTyping(player: PlayerEntity) { networking.sendStartTypingStatus()}

    override fun createMessageFromContext(context: Typer.TypingMessageContext): ChatMessage {
        return ChatMessage(MinecraftClient.getInstance().player!!.name.string, context.processedText).apply {
            setTags(ChatMessageTagsBuilder()
                .components(getLocal<Typer>().parseTags(context.processedText)) as ChatMessageTagsBuilder)
        }
    }

    override fun clearStorage() {
        messageStorage.clear()
    }

    override fun clearSystemMessages() {
        messageStorage.clear { message ->
            message.authorName == SYSTEM_MESSAGE_AUTHOR
        }
    }

    override fun sendMessageToServer(message: ChatMessage) {
        networking.sendMessagePacket(message)
    }

    override fun getStorage() = messageStorage

    override fun addMessage(message: ChatMessage) { messageStorage.addMessage(message) }

    override fun handleMessageFromServer(message: Message) { networking.handleMessagePacket(message) }

    override fun getTypingContextFromText(text: String): Typer.TypingMessageContext {
        return getLocal<Typer>().handleTypingMessage(text)
    }

    override fun getMessageProvider() = messageStorage.provider

    override fun getTextTransformer() = getLocal<ChatTextTransformer>()
}