package org.qbrp.engine.chat.addons.rp

import org.koin.core.component.get
import org.qbrp.core.mc.registry.CommandsRepository
import org.qbrp.core.resources.data.config.ServerConfigData
import org.qbrp.engine.Engine
import org.qbrp.engine.chat.ChatAddon
import org.qbrp.engine.chat.addons.groups.ChatGroupsAPI
import org.qbrp.engine.chat.core.messages.ChatMessageTagsBuilder
import org.qbrp.engine.chat.core.system.ChatGroup
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.LoadPriority
import org.qbrp.system.networking.messages.components.ClusterBuilder
import org.qbrp.system.networking.messages.components.Component

@Autoload(LoadPriority.ADDON)
class RPCommands(): ChatAddon("rp-commands") {
    private val config = get<ServerConfigData>().chat.commands

    companion object {
        val defaultComponents = ChatMessageTagsBuilder()
            .component("format", false)

    }

    override fun onLoad() {
        registerCommands()
    }

    private fun registerCommands() {
        registerDefaultRpCommand("roll", config.formatRoll)
        registerDefaultRpCommand("do", config.formatDo)
        registerDefaultRpCommand("me", config.formatMe)
        CommandsRepository.add(MessageCommand("ldo", createCommandChatGroup(
            name ="ldo",
            format = config.formatLdo,
            radius = 60,
            ClusterBuilder()
                .component("ignoreVolume", true)))
        )
        CommandsRepository.add(MessageCommand("gdo", createCommandChatGroup(
            name = "gdo",
            format = config.formatGdo,
            radius = -1,
            ClusterBuilder()
                .component("ignoreVolume", true)))
        )
    }

    private fun registerDefaultRpCommand(name: String, format: String, components: List<Component> = emptyList()) {
        CommandsRepository.add(
            MessageCommand(name, createCommandChatGroup
                (name, format, components = defaultComponents.components(components)))
        )
    }

    private fun createCommandChatGroup(
        name: String,
        format: String,
        radius: Int = 32,
        components: ClusterBuilder = defaultComponents
    ): ChatGroup {
        val chatGroupsAPI = Engine.getAPI<ChatGroupsAPI>()!!
        return chatGroupsAPI.getGroup(name) ?: ChatGroup(
            name = name,
            format = format,
            radius = radius,
            prefix = "NONE"
        ).apply {
            buildedComponents = components.build().getData().toList()
            chatGroupsAPI.addGroup(this)
        }
    }

}