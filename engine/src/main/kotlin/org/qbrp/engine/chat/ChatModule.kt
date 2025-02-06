package org.qbrp.engine.chat

import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import org.qbrp.core.resources.data.config.ServerConfigData
import org.qbrp.core.visual.VisualDataStorage
import org.qbrp.engine.chat.events.MessageSendEvent
import org.qbrp.engine.chat.events.MessageSenderPipeline
import org.qbrp.engine.chat.messages.ChatException
import org.qbrp.engine.chat.messages.MessageSender
import org.qbrp.engine.chat.system.ChatGroups
import org.qbrp.engine.chat.system.ServerChatNetworking
import org.qbrp.engine.chat.system.MessageHandler
import org.qbrp.engine.chat.system.TextTagsTransformer
import org.qbrp.system.networking.messages.Message
import org.qbrp.system.utils.format.Format.stripFormatting
import org.qbrp.system.utils.log.Loggers

class ChatModule(chatConfig: ServerConfigData.Chat, val server: MinecraftServer) {
    companion object { const val MESSAGE_AUTHOR_SYSTEM = "system" }
    private val logger = Loggers.get("chatModule")
    private val groups = ChatGroups()
    private val handler = MessageHandler(server)
    private val networking = ServerChatNetworking(handler)

    init {
        logger.success("ChatModule загружен")
        groups.loadGroups(chatConfig.chatGroups)
    }

    val API = Api()
    private val groupsModule = Groups()
    private val placeholdersModule = Placeholders()
    private val mentionsModule = Mentions()
    private val spyModule = Spy()

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
            networking.sendGroupsList(player, groups)
        }
        fun networking() = networking
        fun createSender() = MessageSender(networking, mutableListOf())
        fun handleMessagePacket(message: Message) { networking.handleMessagePacket(message) }
    }

    private inner class Groups {
        init {
            MessageSenderPipeline.EVENT.register { message, sender ->
                val groupTag = message.getTags().getComponentData<String>("group")
                val group = groups.getGroup(groupTag ?: "default")
                val author = message.getAuthorEntity(server.playerManager)
                if (group == null) {
                    return@register ChatException(message, "Группа $groupTag не найдена. Доступные группы: ${groups.getAllGroups().map { it.name }.joinToString(", ")}").send()
                }

                message.getTagsBuilder()
                    .placeholder("playerName", author!!.name.string)
                    .placeholder("playerDisplayName", author.displayName.string)
                    .placeholder("text", message.text)
                message.text = group.format

                sender.addTargets(group.getPlayersCanSee(message.getAuthorEntity() as ServerPlayerEntity) as List<ServerPlayerEntity>)
                ActionResult.PASS
            }
        }
    }

    private inner class Mentions {
       init {
           MessageSenderPipeline.EVENT.register { message, sender ->
               message.getTags().getComponentData<String>("mention").let {
                   sender.addTarget(server.playerManager.getPlayer(it) ?: return@register ActionResult.PASS)
               }
               ActionResult.PASS
           }
       }
    }

    private inner class Spy {
        fun getSpyPlayers(): List<ServerPlayerEntity> {
            return server.playerManager.playerList
        }
        init {
            MessageSenderPipeline.EVENT.register { message, sender ->
                val spyPlayers = getSpyPlayers()
                    .filterNot { it.name.string == message.authorName }
                    .toMutableList()
                if (!spyPlayers.isEmpty() && sender.isPlayerInTarget(message.getAuthorEntity(server.playerManager)!!) ) {
                    val spySender = MessageSender(networking, spyPlayers)
                    val spyMessage = message.copy().apply {
                        text = "&6[S]&r $text"
                    }
                    spySender.send(spyMessage)
                }
                ActionResult.PASS
            }
        }
    }

    private inner class Placeholders {
        init {
            MessageSendEvent.EVENT.register { sender, message ->
                val placeholders = message.getTags().getValueComponents("value")
                var updatedText = message.text

                placeholders.forEach { key, value ->
                    updatedText = updatedText.replace("{$key}", value)
                }

                message.text = updatedText
                ActionResult.PASS
            }
        }
    }
}