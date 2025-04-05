package org.qbrp.engine.chat.addons

import net.minecraft.server.network.ServerPlayerEntity
import org.koin.core.component.get
import org.qbrp.core.resources.data.config.ConfigInitializationCallback
import org.qbrp.core.resources.data.config.ServerConfigData
import org.qbrp.engine.Engine
import org.qbrp.engine.chat.ChatAddon
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.engine.chat.core.messages.ChatMessageTagsBuilder
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.LoadPriority
import org.qbrp.system.modules.ModuleAPI
import org.qbrp.system.networking.messages.types.StringContent

@Autoload(priority = LoadPriority.ADDON)
class SystemMessages(): ChatAddon("system-messages"), ModuleAPI {
    private lateinit var config: ServerConfigData.Chat

    init {
        dependsOn { Engine.isApiAvailable<BroadcasterAPI>() }
    }

    override fun load() {
        super.load()
        ConfigInitializationCallback.EVENT.register { updatedConfig ->
            config = updatedConfig.chat
        }
    }

    fun sendJoinMessage(player: ServerPlayerEntity) {
        sendMessage("playerJoin", text = config.joinMessage, player.name.string)
    }

    fun sendLeaveMessage(player: ServerPlayerEntity) {
        sendMessage("playerLeave", text = config.leaveMessage, player.name.string)
    }

    private fun sendMessage(type: String, text: String, player: String) {
        if (!text.isBlank()) {
            Engine.getAPI<BroadcasterAPI>()!!.broadcast(ChatMessage(
                authorName = player,
                text = text,
            ).apply {
                getTagsBuilder()
                    .placeholder("playerName", player)
                    .component("group", StringContent(type)) as ChatMessageTagsBuilder
            })
        }
    }
}