package org.qbrp.engine.chat.addons

import net.minecraft.server.MinecraftServer
import org.qbrp.engine.Engine
import org.qbrp.engine.chat.ChatModule
import org.qbrp.engine.chat.ChatModule.Companion.MESSAGE_AUTHOR_SYSTEM
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.engine.chat.core.system.ChatGroup

class Broadcaster(private val chatGroups: Groups, private val server: MinecraftServer) {
    private val chatModuleAPI = Engine.chatModule.API
    private val broadcastGroup = chatGroups.groupsList.addGroup(
            ChatGroup(
                name = "broadcast",
                simpleName = "вещание",
                color = "#e4717a",
                radius = -1,
                format = "&7~~~&r&d(&f &d)"
            )
        )

    fun broadcastDo(text: String) {
        chatModuleAPI.createSender().apply {
            addTargets(server.playerManager.playerList)
            send(
                ChatMessage(MESSAGE_AUTHOR_SYSTEM, text)
            )
        }
    }
}