package org.qbrp.engine.chat.addons

import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.engine.Engine
import org.qbrp.engine.chat.ChatAPI
import org.qbrp.engine.chat.ChatAddon
import org.qbrp.engine.chat.ChatModule.Companion.SYSTEM_MESSAGE_AUTHOR
import org.qbrp.engine.chat.addons.groups.ChatGroups
import org.qbrp.engine.chat.addons.groups.ChatGroupsAPI
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.engine.chat.core.system.ChatGroup
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.LoadPriority
import org.qbrp.system.modules.ModuleAPI
import org.qbrp.system.networking.messaging.NetworkManager.sendMessage
import org.qbrp.system.utils.log.Loggers

@Autoload(priority = 2)
class Broadcaster: ChatAddon("broadcaster"), BroadcasterAPI {
    lateinit var chatModuleAPI: ChatAPI
    val server: MinecraftServer by inject()

    private val logger = Loggers.get("chat", "broadcast")

    override fun load() {
        chatModuleAPI = Engine.moduleManager.getAPI<ChatAPI>()!!
    }

    override fun getAPI(): BroadcasterAPI = this

    override fun getKoinModule() = module {
        single { ChatGroup(
            name = "broadcast",
            simpleName = "вещание",
            color = "#e4717a",
            radius = -1,
            format = "<light_purple><bold>*<reset> {name} <reset><light_purple>(reset> {text} <light_purple>)<reset>"
        ) }
        single { this }
    }

    override fun broadcastGlobalDo(message: ChatMessage) {
        ChatGroups.handle(message, get<ChatGroup>(), Engine.getAPI<ChatGroupsAPI>()?.getGroup("default")!!)
        chatModuleAPI.createSender().apply {
            addTargets(server.playerManager.playerList)
            send(message)
        }
        logger.log("<<[Do]>> ${message.getText()}")
    }

    override fun broadcast(
        message: ChatMessage,
        targets: List<ServerPlayerEntity>
    ) {
        chatModuleAPI.createSender().apply {
            addTargets(targets)
            send(
                message
            )
        }
    }

    override fun broadcast(message: ChatMessage) {
        broadcast(message, server.playerManager.playerList)
    }

    override fun broadcast(text: String) {
        broadcast(ChatMessage(SYSTEM_MESSAGE_AUTHOR, text), server.playerManager.playerList)
    }
}