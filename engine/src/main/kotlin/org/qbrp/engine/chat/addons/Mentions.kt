package org.qbrp.engine.chat.addons

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import org.koin.core.component.inject
import org.qbrp.core.ServerCore
import org.qbrp.engine.Engine
import org.qbrp.engine.chat.ChatAddon
import org.qbrp.engine.chat.addons.groups.ChatGroupsAPI
import org.qbrp.engine.chat.addons.tools.MessageTextTools
import org.qbrp.engine.chat.core.events.MessageReceivedEvent
import org.qbrp.engine.chat.core.events.MessageSenderPipeline
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.engine.chat.core.system.TextTagsTransformer
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.LoadPriority
import org.qbrp.system.modules.ModuleAPI
import org.qbrp.system.utils.format.Format.formatMinecraft

@Autoload(LoadPriority.ADDON, both = true)
class Mentions(): ChatAddon("mentions") {
    private val server: MinecraftServer by inject()

    override fun load() {
        MessageReceivedEvent.EVENT.register { message ->
            val pattern = Regex("@[A-Za-z0-9_]+")
            message.getTagsBuilder().apply {
                pattern.findAll(MessageTextTools.getTextContent(message)).forEach { match ->
                    val username = match.value.substring(1)
                    if (ServerCore.server.playerManager.getPlayer(username) != null
                        || username == "everyone"
                        || username == "here") {
                        textComponent("mention", username, message) { text, component ->
                            text.replace(match.value, component)
                        }
                    }
                }
            }
            ActionResult.PASS
        }

        MessageSenderPipeline.EVENT.register { message, sender ->
            message.getTags().getComponentsData<String>("mention")?.forEach {
                if (it == "everyone") {
                    sender.addTargets(server.playerManager.playerList)
                } else if (it == "here") {
                    sender.addTargets((Engine.getAPI<ChatGroupsAPI>()!!
                        .fetchGroup(message)?.getPlayersCanSee(message.getAuthorEntity()!!) ?: emptyList()) as List<ServerPlayerEntity>)
                } else {
                    sender.addTarget(server.playerManager.getPlayer(it) ?: return@forEach)
                }
            }
            MessageTextTools.setTextContent(message, TextTagsTransformer.replaceTagsWithFormat(
                MessageTextTools.getTextContent(message), "mention") { tag, value ->
                "<bold><yellow>@${value}</yellow></bold>"
            })
            ActionResult.PASS
        }
    }
}