 package org.qbrp.main.engine.chat

import net.minecraft.server.MinecraftServer
import org.koin.core.component.inject
import org.qbrp.deprecated.resources.data.config.ServerConfigData
import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.engine.chat.core.messages.MessageSender
import org.qbrp.main.engine.chat.core.system.MessageHandler
import org.qbrp.main.engine.chat.core.system.ServerChatNetworking
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.util.networking.messages.Message
import org.qbrp.main.engine.players.model.ServerPlayerObject

 @Autoload(6, both = true)
class ChatModule() : EngineModule("chat"), ChatAPI {
    private val serverChatNetworking: ServerChatNetworking by lazy { getLocal() }
    private val server: MinecraftServer by inject()

    override fun getName(): String = "chat"

    override fun getKoinModule() = inner<ChatAPI>(this) {
        scoped { get<ServerConfigData>().chat }
        scoped { MessageHandler(get()) }
        scoped { ServerChatNetworking(get(), get()) }
        scoped<ChatModule> { this@ChatModule }
    }

    override fun createSender() = MessageSender(serverChatNetworking, server)
    override fun loadAddon(addon: ChatAddon) { }

    override fun sendMessage(player: ServerPlayerObject, message: ChatMessage) =
        createSender()
            .apply { addTarget(player) }
            .send(message)

    override fun sendMessage(player: ServerPlayerObject, message: String, authorName: String) =
        createSender()
            .apply { addTarget(player) }
            .send(ChatMessage(authorName, message))

    override fun handleMessage(message: ChatMessage) {
        getLocal<MessageHandler>().handleReceivedMessage(message, getLocal())
    }

    fun handleMessagePacket(message: Message) = getLocal<ServerChatNetworking>().handleMessagePacket(message)

    companion object {
        const val SYSTEM_MESSAGE_AUTHOR = "system"
    }
}
