package org.qbrp.main.engine.chat.addons

import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import org.koin.core.component.get
import org.koin.core.component.inject
import org.qbrp.main.core.mc.player.PlayersModule
import org.qbrp.main.core.mc.player.PlayersUtil
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.chat.ChatAddon
import org.qbrp.main.engine.chat.addons.groups.ChatGroupsAPI
import org.qbrp.main.engine.chat.addons.tools.MessageTextTools
import org.qbrp.main.engine.chat.core.events.MessageReceivedEvent
import org.qbrp.main.engine.chat.core.events.MessageSenderPipeline
import org.qbrp.main.engine.chat.core.system.TextTagsTransformer
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.LoadPriority

@Autoload(LoadPriority.ADDON, both = true)
class Mentions(): ChatAddon("mentions") {
    private val server: MinecraftServer by inject()

    override fun onLoad() {
        MessageReceivedEvent.EVENT.register { message ->
            val pattern = Regex("@[A-Za-z0-9_]+")
            message.getTagsBuilder().apply {
                pattern.findAll(MessageTextTools.getTextContent(message)).forEach { match ->
                    val username = match.value.substring(1)
                    if (PlayersUtil.getPlayer(username) != null
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
                    val playersCanSee = get<ChatGroupsAPI>().fetchGroup(message)?.getPlayersCanSee(message.getAuthorEntity()!!) ?: emptyList()
                    @Suppress("UNCHECKED_CAST") sender.addTargets(playersCanSee as List<ServerPlayerEntity>)
                } else {
                    sender.addTarget(PlayersUtil.getPlayer(it!!) ?: return@forEach)
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