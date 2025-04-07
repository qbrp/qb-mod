package org.qbrp.engine.client.engine.chat

import net.fabricmc.api.EnvType
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.koin.core.component.get
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.core.visual.VisualDataStorage
import org.qbrp.engine.chat.ChatModule.Companion.SYSTEM_MESSAGE_AUTHOR
import org.qbrp.engine.chat.core.system.ChatGroup
import org.qbrp.engine.chat.core.system.ChatGroupsStorage
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.engine.chat.core.messages.ChatMessageTagsBuilder
import org.qbrp.engine.chat.core.system.TextTagsTransformer
import org.qbrp.engine.client.engine.chat.system.ChatTextTransformer
import org.qbrp.engine.client.engine.chat.system.ClientChatNetworking
import org.qbrp.engine.client.engine.chat.system.MessageStorage
import org.qbrp.engine.client.engine.chat.system.Typer
import org.qbrp.engine.chat.core.events.ChatFormatEvent
import org.qbrp.engine.client.engine.chat.system.LinearMessageProvider
import org.qbrp.engine.client.engine.chat.system.Provider
import org.qbrp.engine.client.engine.chat.system.events.MessageAddedEvent
import org.qbrp.engine.client.engine.chat.system.events.MessageSendEvent
import org.qbrp.engine.client.engine.chat.system.events.TextUpdateCallback
import org.qbrp.engine.client.system.networking.ClientNetworkManager
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.ModuleAPI
import org.qbrp.system.modules.QbModule
import org.qbrp.system.networking.ServerInformation
import org.qbrp.system.networking.messages.Message
import org.qbrp.system.networking.messages.components.readonly.ClusterViewer
import java.util.concurrent.TimeUnit
import kotlin.concurrent.fixedRateTimer
import kotlin.math.atan2
import kotlin.random.Random

@Autoload(10, env = EnvType.CLIENT)
class ChatModuleClient(): QbModule("chat-client"), ClientChatAPI {
    lateinit var messageStorage: MessageStorage
    lateinit var networking: ClientChatNetworking

    companion object {
        const val TEXT_UPDATE_TICK_RATE = 5
        var MAX_MESSAGE_LENGTH = 456
    }

    override fun getKoinModule() = module {
        single { MessageStorage() }
        single { ClientChatNetworking(get()) }
        single { ChatTextTransformer() }
        single { Typer() }
    }

    override fun getAPI(): ClientChatAPI = this

    override fun load() {
        messageStorage = get<MessageStorage>()
        networking = get<ClientChatNetworking>().apply {
            registerReceivers()
        }
        MAX_MESSAGE_LENGTH = ServerInformation.VIEWER?.getComponentData<Int>("engine.maxMessageLength") ?: 456
    }

    override fun isPlayerWriting(player: PlayerEntity): Boolean { return VisualDataStorage.getPlayer(player.name.string)?.isWriting == true }

    override fun getWritingPlayers(world: World): List<PlayerEntity> {
        return world.players
            .mapNotNull { player ->
                val data = VisualDataStorage.getPlayer(player.name.string)
                if (data?.isWriting == true) player else null
            }
    }
    override fun endTyping(player: PlayerEntity) { networking.sendEndTypingStatus() }
    override fun startTyping(player: PlayerEntity) { networking.sendStartTypingStatus()}

    override fun createMessageFromContext(context: Typer.TypingMessageContext): ChatMessage {
        return ChatMessage(MinecraftClient.getInstance().player!!.name.string, context.processedText).apply {
            setTags(ChatMessageTagsBuilder()
                .components(get<Typer>().parseTags(context.processedText)) as ChatMessageTagsBuilder)
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
        return get<Typer>().handleTypingMessage(text)
    }

    override fun getMessageProvider() = messageStorage.provider

    override fun getTextTransformer() = get<ChatTextTransformer>()
}