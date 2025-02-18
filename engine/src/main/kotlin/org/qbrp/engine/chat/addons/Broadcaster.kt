package org.qbrp.engine.chat.addons

import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import org.qbrp.engine.Engine
import org.qbrp.engine.chat.ChatModule
import org.qbrp.engine.chat.ChatModule.Companion.MESSAGE_AUTHOR_SYSTEM
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.engine.chat.core.system.ChatGroup
import org.qbrp.system.utils.format.Format.formatMinecraft
import org.qbrp.system.utils.log.Loggers

class Broadcaster(private val chatGroups: Groups, private val server: MinecraftServer): VanillaChatBroadcaster {
    private val chatModuleAPI by lazy { Engine.chatModule.API }
    private val broadcastGroup = chatGroups.groupsList.addGroup(
            ChatGroup(
                name = "broadcast",
                simpleName = "вещание",
                color = "#e4717a",
                radius = -1,
                format = "&7~~~&r&d(&f &d)"
            )
        )
    private val logger = Loggers.get("chat", "broadcast")

    override fun broadcast(message: Text) {
        broadcast(message, server.playerManager.playerList)
    }

    override fun broadcast(
        message: Text,
        targets: List<ServerPlayerEntity>
    ) {
        logger.log("<<[Broadcast]>> ${message.string}")
        targets.forEach { it.sendMessage(message) }
    }

    fun broadcast(text: String) {
        chatModuleAPI.createSender().apply {
            addTargets(server.playerManager.playerList)
            send(
                ChatMessage(MESSAGE_AUTHOR_SYSTEM, text)
            )
        }
    }

    fun broadcast(message: ChatMessage) {
        chatModuleAPI.createSender().apply {
            addTargets(server.playerManager.playerList)
            send(message)
        }
    }
}