package org.qbrp.main.engine.chat.addons.spy

import PermissionsUtil.hasPermission
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.chat.ChatAddon
import org.qbrp.main.engine.chat.ChatModule
import org.qbrp.main.engine.chat.addons.groups.ChatGroupsAPI
import org.qbrp.main.engine.chat.core.events.MessageHandledEvent

import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.LoadPriority


@Autoload(LoadPriority.ADDON)
class Spy(): ChatAddon("spy") {
    private val server: MinecraftServer by inject()
    private val chatGroupsAPI = Engine.getAPI<ChatGroupsAPI>()
    private lateinit var spyManager: SpyManager

    override fun onLoad() {
        super.onLoad()
        spyManager = getLocal()

        MessageHandledEvent.EVENT.register { message, receivers ->
            if (message.authorName == ChatModule.SYSTEM_MESSAGE_AUTHOR) return@register ActionResult.PASS
            if (message.getTags().isComponentExists("spy") == true) return@register ActionResult.PASS
            if (message.getAuthorEntity() == null) return@register ActionResult.PASS
            val spyPlayers = getSpyPlayers()
                .filterNot { it.name.string == message.authorName }
                .filterNot { it in receivers }
                .filter { chatGroupsAPI!!.fetchGroup(message)?.playerHasReadPermission(it) == true }
                .filter { it.hasPermission("chat.spy") }
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

    fun getSpyPlayers(): List<ServerPlayerEntity> {
        return server.playerManager.playerList
    }
}