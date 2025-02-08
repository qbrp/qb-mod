package org.qbrp.engine.chat

import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.core.resources.data.config.ServerConfigData
import org.qbrp.core.visual.VisualDataStorage
import org.qbrp.engine.chat.addons.Broadcaster
import org.qbrp.engine.chat.addons.Groups
import org.qbrp.engine.chat.addons.Mentions
import org.qbrp.engine.chat.addons.Placeholders
import org.qbrp.engine.chat.addons.Spy
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.engine.chat.core.messages.MessageSender
import org.qbrp.engine.chat.core.system.ServerChatNetworking
import org.qbrp.engine.chat.core.system.MessageHandler
import org.qbrp.system.networking.messages.Message
import org.qbrp.system.utils.log.Loggers

class ChatModule(val chatConfig: ServerConfigData.Chat, val server: MinecraftServer) {
    companion object { const val MESSAGE_AUTHOR_SYSTEM = "system" }
    private val logger = Loggers.get("chatModule")
    private val handler = MessageHandler(server)
    private val networking = ServerChatNetworking(handler)

    init {
        logger.success("ChatModule загружен")
    }

    private val addons = Addons()
    val API = Api()

    inner class Api {
        fun playerStartTyping(player: ServerPlayerEntity) {
            VisualDataStorage.getPlayer(player.name.string)?.apply {
                isWriting = true
                broadcastHardUpdate()
            }
        }
        fun playerEndTyping(player: ServerPlayerEntity) {
            VisualDataStorage.getPlayer(player.name.string)?.apply {
                isWriting = false
                broadcastHardUpdate()
            }
        }

        fun sendDataToJoinedPlayer(player: ServerPlayerEntity) {
            networking.sendGroupsList(player, addons.groups.groupsList)
        }
        fun getNetworking() = networking
        fun createSender() = MessageSender(networking, mutableListOf())
        fun handleMessagePacket(message: Message) { networking.handleMessagePacket(message) }
        fun handleMessage(message: ChatMessage) { handler.handleReceivedMessage(message, networking) }
        fun getAddons() = addons
    }

    inner class Addons() {
        val groups = Groups(server, chatConfig)
        private val mentions = Mentions(server)
        private val placeholders = Placeholders()
        private val spy = Spy(server, networking)
        val broadcaster = Broadcaster(groups, server)
    }
}