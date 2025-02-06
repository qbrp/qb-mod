package org.qbrp.engine.client.engine.chat

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.hud.ChatHudLine
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.world.World
import org.qbrp.core.visual.VisualDataStorage
import org.qbrp.engine.chat.events.MessageSendEvent
import org.qbrp.engine.chat.system.ChatGroup
import org.qbrp.engine.chat.system.ChatGroups
import org.qbrp.engine.chat.messages.ChatMessage
import org.qbrp.engine.chat.messages.ChatMessageTagsBuilder
import org.qbrp.engine.chat.messages.ChatMessageTagsCluster
import org.qbrp.engine.chat.system.TextTagsTransformer
import org.qbrp.engine.client.engine.chat.system.ChatTextTransformer
import org.qbrp.engine.client.engine.chat.system.ClientChatNetworking
import org.qbrp.engine.client.engine.chat.system.MessageStorage
import org.qbrp.engine.client.engine.chat.system.Typer
import org.qbrp.engine.client.engine.chat.system.events.ChatFormatEvent
import org.qbrp.engine.client.engine.chat.system.events.ChatInputTransformEvent
import org.qbrp.system.networking.messages.Message
import org.qbrp.system.networking.messages.components.ClusterBuilder
import org.qbrp.system.networking.messages.components.readonly.ClusterViewer
import org.qbrp.system.utils.format.Format.stripFormatting

class ChatModuleClient() {
    private val chatGroups = ChatGroups()
    private val messageStorage = MessageStorage()
    private val networking = ClientChatNetworking(messageStorage)
    private val messageTransformer: ChatTextTransformer = ChatTextTransformer()
    private val typer = Typer()

    val api = API()
    val mentions = Mentions()

    inner class Mentions {
        fun transformMentions(text: String): String {
            val playerNames: List<String> = MinecraftClient.getInstance().player
                ?.networkHandler
                ?.playerList
                ?.map { it.profile.name }
                ?: emptyList()
            val mentionRegex = Regex("@(\\w+)")

            return mentionRegex.replace(text) { matchResult ->

                val nick = matchResult.groupValues[1]
                if (playerNames.any { it.equals(nick, ignoreCase = true) }) {
                    """<mention:"$nick">"""
                } else {
                    matchResult.value
                }
            }
        }

        init {
            ChatFormatEvent.EVENT.register { message ->
                message.text = TextTagsTransformer.replaceTagsWithFormat(message.text, "mention") { tag, value ->
                    "&e&l@${value?.replace("\"", "")?.stripFormatting()}&r"
                }
                ActionResult.PASS
            }
            ChatInputTransformEvent.EVENT.register { text ->
                return@register transformMentions(text)
            }
        }
    }

    inner class API() {
        fun isPlayerWriting(player: PlayerEntity): Boolean { return VisualDataStorage.getPlayer(player.name.string)?.isWriting == true }
        fun getWritingPlayers(world: World): List<PlayerEntity> {
            return world.players
                .mapNotNull { player ->
                    val data = VisualDataStorage.getPlayer(player.name.string)
                    if (data?.isWriting == true) player else null
                }
        }
        fun endTyping(player: PlayerEntity) { networking.sendEndTypingStatus() }
        fun startTyping(player: PlayerEntity) { networking.sendStartTypingStatus()}

        fun getMessages(): List<ChatHudLine.Visible> {
            return messageStorage.calculateComputedMessages()
        }

        fun createMessageFromContext(context: Typer.TypingMessageContext): ChatMessage {
            return ChatMessage(MinecraftClient.getInstance().player!!.name.string, context.processedText,
                ChatMessageTagsBuilder().components(typer.parseTags(context.processedText)) as ChatMessageTagsBuilder
            )
        }

        fun sendMessageToServer(message: ChatMessage) {
            networking.sendMessagePacket(message)
        }

        fun handleMessageFromServer(message: Message) { networking.handleMessagePacket(message) }

        fun createChatGroupFromCluster(cluster: ClusterViewer): ChatGroup {
            return ChatGroup(
                cluster.getComponentData<String>("name")!!,
                cluster.getComponentData<String>("simpleName")!!,
                cluster.getComponentData<String>("prefix")!!,
                cluster.getComponentData<String>("color")!!
            )
        }

        fun getTypingContextFromText(text: String): Typer.TypingMessageContext {
            return typer.handleTypingMessage(text)
        }

        fun getTextTransformer() = messageTransformer

        fun loadChatGroups(groups: List<ChatGroup>) {
            chatGroups.loadGroups(groups)
        }

        fun getChatGroups(): ChatGroups {
            return chatGroups
        }
    }
}