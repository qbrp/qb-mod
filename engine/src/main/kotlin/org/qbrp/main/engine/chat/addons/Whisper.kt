package org.qbrp.main.engine.chat.addons

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import org.koin.dsl.module
import org.qbrp.main.core.mc.commands.CommandRegistryEntry
import org.qbrp.main.core.mc.commands.CommandRegistryEntry.Companion.requirePlayer
import org.qbrp.main.core.mc.player.PlayersUtil
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.chat.ChatAddon
import org.qbrp.main.engine.chat.addons.groups.ChatGroups
import org.qbrp.main.engine.chat.addons.groups.ChatGroupsAPI
import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.engine.chat.core.messages.MessageComponent
import org.qbrp.main.engine.chat.core.system.ChatGroup
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.LoadPriority
import org.qbrp.main.core.utils.format.Format.asMiniMessage
import org.koin.core.component.get
import org.qbrp.main.core.mc.commands.CommandsAPI

@Autoload(LoadPriority.ADDON)
class Whisper: ChatAddon("whisper"), CommandRegistryEntry {
    init {
        dependsOn { Engine.isApiAvailable<ChatGroupsAPI>() }
    }

    override fun getKoinModule() = module {
        single {
            ChatGroup(
                name = "whisper",
                simpleName = "шёпот",
                color = "#f8f471",
                radius = 0,
                format = "<bold><yellow>#</yellow></bold> {playerRpName} <yellow>▸</yellow> <gray>{text}",
                components = listOf(MessageComponent("whisper", true))
            )
        }
    }

    override fun onLoad() {
        super.onLoad()
        get<CommandsAPI>().add(this)
    }

    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val node = dispatcher.register(CommandManager.literal("whisper")
            .then(CommandManager.argument("text", StringArgumentType.greedyString())
                .executes() { ctx ->
                    try {
                        val author = PlayersUtil.getPlayerSession(requirePlayer(ctx) ?: return@executes 0)
                        val target = PlayersUtil.getPlayerLookingAt(author) ?: run {
                            ctx.source.sendMessage("<red>Вы не смотрите на игрока.".asMiniMessage())
                            return@executes 0
                        }
                        val message = ChatMessage(author.entityName, StringArgumentType.getString(ctx, "text"))
                        ChatGroups.handle(message, Engine.getAPI<ChatGroupsAPI>()?.getGroup("whisper") ?: getLocal<ChatGroup>())

                        chatAPI.createSender().apply {
                            addTarget(author.entity)
                            addTarget(target)
                            send(message)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    1
                }))
        dispatcher.register(CommandManager.literal("w").redirect(node))
    }

}