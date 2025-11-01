package org.qbrp.main.engine.chat.addons.spy

import net.minecraft.server.MinecraftServer
import net.minecraft.util.ActionResult
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.main.core.fabricmc.player.Permission
import org.qbrp.main.core.fabricmc.player.hasPermission
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.chat.ChatAddon
import org.qbrp.main.engine.chat.ChatModule
import org.qbrp.main.engine.chat.addons.groups.ChatGroupsAPI
import org.qbrp.main.engine.chat.core.events.MessageHandledEvent

import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.engine.players.model.service.ServerPlayerStorage


@Autoload(Priorities.ADDON)
class Spy(): ChatAddon("spy") {
    private val server: MinecraftServer by inject()
    private val playerStorage: ServerPlayerStorage by inject()
    private val chatGroupsAPI = Engine.getAPI<ChatGroupsAPI>()
    private lateinit var spyManager: SpyManager

    override fun onLoad() {
        super.onLoad()
        spyManager = getLocal()

        MessageHandledEvent.EVENT.register { message, receivers ->
            if (message.authorName == ChatModule.SYSTEM_MESSAGE_AUTHOR) return@register ActionResult.PASS
            if (message.getTags().isComponentExists("spy")) return@register ActionResult.PASS
            if (message.getAuthorEntity() == null) return@register ActionResult.PASS
            val spyPlayers = playerStorage
                .filterNot { it.playerId == message.author?.playerId }
                .filterNot { it in receivers }
                .filter { chatGroupsAPI!!.fetchGroup(message)?.playerHasReadPermission(it) == true }
                .filter { it.hasPermission(Permission("chat.spy")) }
                .toMutableList()
            if (!spyPlayers.isEmpty()) {
                val sender = chatAPI.createSender().apply {
                    addTargets(spyPlayers)
                }
                val spyMessage = message.copy().apply {
                    setText("&6[S]&r ${getText().replace("{radar}", "")}")
                    setTags(getTagsBuilder()
                        .component("sound", "")
                        .component("spy", true)
                        .component("handleVolume", false)
                        .component("spectators", false)
                        .component("radar", false))
                    handleUpdate()
                }
                sender.send(spyMessage)
            }
            ActionResult.PASS
        }
    }

    override fun getKoinModule(): Module = module {
        single { SpyManager() }
    }
}