package org.qbrp.client.engine.chat

import eu.midnightdust.lib.config.MidnightConfig
import net.fabricmc.api.EnvType
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import org.koin.core.component.get
import org.lwjgl.glfw.GLFW
import org.qbrp.client.ClientCore
import org.qbrp.client.core.keybinds.ClientKeybindsAPI
import org.qbrp.main.engine.chat.ChatModule.Companion.SYSTEM_MESSAGE_AUTHOR
import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.engine.chat.core.messages.ChatMessageTagsBuilder
import org.qbrp.client.engine.chat.system.ChatTextTransformer
import org.qbrp.client.engine.chat.system.ClientChatNetworking
import org.qbrp.client.engine.chat.system.MessageStorage
import org.qbrp.client.engine.chat.system.Typer
import org.qbrp.client.engine.chat.system.logs.ChatLogger
import org.qbrp.main.core.Core
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.QbModule
import org.qbrp.main.core.info.ServerInformationGetEvent
import org.qbrp.main.core.utils.networking.messages.Message
import org.qbrp.main.engine.chat.ChatModule

@Autoload(env = EnvType.CLIENT)
class ChatModuleClient(): QbModule("chat-client"), ClientChatAPI {
    lateinit var messageStorage: MessageStorage
    lateinit var networking: ClientChatNetworking

    init {
        ClientCore.isApiAvailable<ClientKeybindsAPI>()
        createModuleFileOnInit()
    }

    companion object {
        var MAX_MESSAGE_LENGTH = 256
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
        ServerInformationGetEvent.EVENT.register {
            MAX_MESSAGE_LENGTH = it.getEntry(ChatModule.MAX_MESSAGE_LENGTH_ENTRY)!!
        }
        registerKeybindings()
    }

    fun registerKeybindings() {
        get<ClientKeybindsAPI>().registerKeybinding(
            "Очистить чат от системных сообщений",
            GLFW.GLFW_KEY_L,
            "clear_system_msgs",
            { ClientCore.getAPI<ClientChatAPI>()?.clearSystemMessages()}
        )
        get<ClientKeybindsAPI>().registerKeybinding(
            "Настройки",
            GLFW.GLFW_KEY_EQUAL,
            "settings",
            { MinecraftClient.getInstance().setScreen(
                MidnightConfig.getScreen(MinecraftClient.getInstance().currentScreen, Core.MOD_ID))
            }
        )
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