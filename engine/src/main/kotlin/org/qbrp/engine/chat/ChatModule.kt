package org.qbrp.engine.chat

import net.minecraft.server.network.ServerPlayerEntity
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.createScope
import org.koin.core.component.get
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.qbrp.core.ServerCore
import org.qbrp.core.resources.ServerResources
import org.qbrp.core.resources.data.config.ServerConfigData
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.engine.chat.core.messages.MessageSender
import org.qbrp.engine.chat.core.system.MessageHandler
import org.qbrp.engine.chat.core.system.ServerChatNetworking
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.QbModule
import org.qbrp.system.modules.ModuleAPI
import org.qbrp.system.networking.messages.Message
import org.qbrp.system.utils.log.Loggers

@Autoload
class ChatModule() : QbModule("chat"), ChatAPI {
    companion object {
        const val SYSTEM_MESSAGE_AUTHOR = "system"
    }

    private val logger = Loggers.get("chatModule")

    override fun getAPI(): ChatAPI = this

    override fun getName(): String = "chat"

    override fun getKoinModule() = module {
        single { ServerResources.getConfig().chat }
        single { MessageHandler(get()) }
        single { ServerChatNetworking(get()) }
        single<ChatModule> { this@ChatModule }
    }

    override fun createSender() = MessageSender(get<ServerChatNetworking>())
    override fun loadAddon(addon: ChatAddon) { }

    override fun sendMessage(player: ServerPlayerEntity, message: ChatMessage) =
        createSender()
            .apply { addTarget(player) }
            .send(message)

    override fun sendMessage(player: ServerPlayerEntity, message: String) =
        createSender()
            .apply { addTarget(player) }
            .send(ChatMessage(SYSTEM_MESSAGE_AUTHOR, message))

    override fun handleMessage(message: ChatMessage) = get<MessageHandler>().handleReceivedMessage(message, get())

    fun handleMessagePacket(message: Message) = get<ServerChatNetworking>().handleMessagePacket(message)
}
