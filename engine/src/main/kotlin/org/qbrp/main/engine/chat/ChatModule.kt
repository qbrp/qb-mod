 package org.qbrp.main.engine.chat

import net.minecraft.server.network.ServerPlayerEntity
import org.koin.core.context.GlobalContext
import org.qbrp.deprecated.resources.data.config.ServerConfigData
import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.engine.chat.core.messages.MessageSender
import org.qbrp.main.engine.chat.core.system.MessageHandler
import org.qbrp.main.engine.chat.core.system.ServerChatNetworking
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.QbModule
import org.qbrp.main.core.info.ServerInfoAPI
import org.qbrp.main.core.utils.networking.messages.Message
import org.qbrp.main.core.utils.networking.messages.components.ClusterEntry
import org.qbrp.main.core.utils.log.LoggerUtil

@Autoload(6, both = true)
class ChatModule() : QbModule("chat"), ChatAPI {
    companion object {
        const val SYSTEM_MESSAGE_AUTHOR = "system"
        var MAX_MESSAGE_LENGTH = 456
        val MAX_MESSAGE_LENGTH_ENTRY = ClusterEntry<Int>("engine.chat.maxMessageLength",)
    }

    private val logger = LoggerUtil.get("chatModule")

    override fun onLoad() {
        GlobalContext.get().getOrNull<ServerInfoAPI>()?.COMPOSER?.component(MAX_MESSAGE_LENGTH_ENTRY, MAX_MESSAGE_LENGTH)
    }

    override fun getName(): String = "chat"

    override fun getKoinModule() = inner<ChatAPI>(this) {
        scoped { get<ServerConfigData>().chat }
        scoped { MessageHandler(get()) }
        scoped { ServerChatNetworking(get()) }
        scoped<ChatModule> { this@ChatModule }
    }

    override fun createSender() = MessageSender(getLocal<ServerChatNetworking>())
    override fun loadAddon(addon: ChatAddon) { }

    override fun sendMessage(player: ServerPlayerEntity, message: ChatMessage) =
        createSender()
            .apply { addTarget(player) }
            .send(message)

    override fun sendMessage(player: ServerPlayerEntity, message: String, authorName: String) =
        createSender()
            .apply { addTarget(player) }
            .send(ChatMessage(authorName, message))

    override fun handleMessage(message: ChatMessage) {
        getLocal<MessageHandler>().handleReceivedMessage(message, getLocal())
    }

    fun handleMessagePacket(message: Message) = getLocal<ServerChatNetworking>().handleMessagePacket(message)
}
