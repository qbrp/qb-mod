package org.qbrp.main.engine.chat.addons

import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.deprecated.resources.data.config.ServerConfigData
import org.qbrp.main.core.fabricmc.commands.ServerCommandDispatcher
import org.qbrp.main.core.util.networking.messages.components.ClusterBuilder
import org.qbrp.main.core.util.networking.messages.components.Component
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.chat.ChatAPI
import org.qbrp.main.engine.chat.addons.groups.ChatGroupsAPI
import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.engine.chat.core.messages.ChatMessageTagsBuilder
import org.qbrp.main.engine.chat.core.system.ChatGroup
import kotlin.random.Random

private val DEFAULT_COMPONENTS = ChatMessageTagsBuilder()
    .component("format", false)

fun ServerCommandDispatcher.registerRpCommands(config: ServerConfigData.Chat.Commands) {
    registerDefaultRpCommand("roll", config.formatRoll)
    registerDefaultRpCommand("do", config.formatDo)
    registerDefaultRpCommand("me", config.formatMe)
    registerMessageCommand(
        "ldo",
        createCommandChatGroup(
            name ="ldo",
            format = config.formatLdo,
            radius = 60,
            components = ClusterBuilder()
                .component("ignoreVolume", true)
        )
    )
    registerMessageCommand(
        "gdo",
        createCommandChatGroup(
            name = "gdo",
            format = config.formatGdo,
            radius = -1,
            ClusterBuilder()
                .component("ignoreVolume", true)
        )
    )
}

fun ServerCommandDispatcher.registerMessageCommand(
    name: String,
    group: ChatGroup
) {
    register(
        CommandManager.literal(name)
            .then(CommandManager.argument("action", StringArgumentType.greedyString())
                    .executes { context ->
                        val args = StringArgumentType.getString(context, "action")
                        context.source.player!!.server.execute {
                            group.sendAction(context.source.player!!, args)
                        }
                        1
                    }
            )
    )
}

private fun ChatGroup.sendAction(player: ServerPlayerEntity, text: String) {
    try {
        val message = ChatMessage(player.name.string, text).apply {
            getTagsBuilder()
                .placeholder("roll", Random.nextInt(0, 101).toString())
                .component("group", name)
        }
        Engine.getAPI<ChatAPI>()!!.handleMessage(message)
    } catch (ex: IllegalArgumentException) {
        ex.printStackTrace()
    }
}

private fun ServerCommandDispatcher.registerDefaultRpCommand(name: String, format: String, components: List<Component> = emptyList()) {
    registerMessageCommand(
        name,
        createCommandChatGroup(
            name = name,
            format = format,
            components = DEFAULT_COMPONENTS.components(components)
        )
    )
}

private fun createCommandChatGroup(
    name: String,
    format: String,
    radius: Int = 32,
    components: ClusterBuilder = DEFAULT_COMPONENTS
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