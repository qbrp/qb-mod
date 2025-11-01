package org.qbrp.main.engine.chat.addons

import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.chat.ChatAPI
import org.qbrp.main.engine.chat.ChatAddon
import org.qbrp.main.engine.chat.ChatModule.Companion.SYSTEM_MESSAGE_AUTHOR
import org.qbrp.main.engine.chat.addons.groups.ChatGroups
import org.qbrp.main.engine.chat.addons.groups.ChatGroupsAPI
import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.engine.chat.core.system.ChatGroup
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.util.log.LoggerUtil
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.service.ServerPlayerStorage

@Autoload(priority = 2)
class Broadcaster: ChatAddon("broadcaster"), BroadcasterAPI {
    private val chatModuleAPI: ChatAPI by inject()
    private val playerList: ServerPlayerStorage by inject()

    private val logger = LoggerUtil.get("chat", "broadcast")

    override fun getKoinModule() = inner<BroadcasterAPI>(this) {
        scoped(named("broadcastGroup")) {
            ChatGroup(
                name = "broadcast",
                simpleName = "вещание",
                color = "#e4717a",
                radius = -1,
                format = "<light_purple><bold>*<reset> {label} <reset><light_purple>(<reset> {chatText} <light_purple>)<reset>"
            )
        }
        scoped { this }
    }

    override fun broadcastGlobalDo(message: ChatMessage) {
        ChatGroups.handle(message, get<ChatGroup>(named("broadcastGroup")), Engine.getAPI<ChatGroupsAPI>()?.getGroup("default")!!)
        chatModuleAPI.createSender().apply {
            addTargets(playerList.getAll())
            send(message)
        }
        logger.log("<<[Do]>> ${message.getText()}")
    }

    override fun broadcast(
        message: ChatMessage,
        targets: List<ServerPlayerObject>
    ) {
        chatModuleAPI.createSender().apply {
            addTargets(targets)
            send(
                message
            )
        }
    }

    override fun broadcast(message: ChatMessage) {
        broadcast(message, playerList.getAll().toList())
    }

    override fun broadcast(text: String) {
        broadcast(ChatMessage(SYSTEM_MESSAGE_AUTHOR, text), playerList.getAll().toList())
    }
}