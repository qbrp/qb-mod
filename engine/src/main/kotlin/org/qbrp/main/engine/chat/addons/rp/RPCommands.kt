package org.qbrp.main.engine.chat.addons.rp

import org.qbrp.main.core.mc.commands.CommandsModule
import org.qbrp.deprecated.resources.data.config.ServerConfigData
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.chat.ChatAddon
import org.qbrp.main.engine.chat.addons.groups.ChatGroupsAPI
import org.qbrp.main.engine.chat.core.messages.ChatMessageTagsBuilder
import org.qbrp.main.engine.chat.core.system.ChatGroup
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.LoadPriority
import org.qbrp.main.core.utils.networking.messages.components.ClusterBuilder
import org.qbrp.main.core.utils.networking.messages.components.Component
import org.koin.core.component.get
import org.qbrp.main.core.mc.commands.CommandsAPI

@Autoload(LoadPriority.ADDON)
class RPCommands(): ChatAddon("rp-commands") {
    private val config = getLocal<ServerConfigData>().chat.commands

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
        get<CommandsAPI>().add(MessageCommand("ldo", createCommandChatGroup(
            name ="ldo",
            format = config.formatLdo,
            radius = 60,
            ClusterBuilder()
                .component("ignoreVolume", true)))
        )
        get<CommandsAPI>().add(MessageCommand("gdo", createCommandChatGroup(
            name = "gdo",
            format = config.formatGdo,
            radius = -1,
            ClusterBuilder()
                .component("ignoreVolume", true)))
        )
    }

    private fun registerDefaultRpCommand(name: String, format: String, components: List<Component> = emptyList()) {
        get<CommandsAPI>().add(
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