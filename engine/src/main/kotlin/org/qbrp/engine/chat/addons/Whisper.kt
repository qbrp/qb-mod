package org.qbrp.engine.chat.addons

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import org.koin.core.component.get
import org.koin.dsl.module
import org.qbrp.core.mc.player.PlayerManager
import org.qbrp.core.mc.registry.CommandsRepository
import org.qbrp.core.mc.registry.ServerModCommand
import org.qbrp.core.mc.registry.ServerModCommand.Companion.requirePlayer
import org.qbrp.engine.Engine
import org.qbrp.engine.chat.ChatAddon
import org.qbrp.engine.chat.addons.groups.ChatGroups
import org.qbrp.engine.chat.addons.groups.ChatGroupsAPI
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.engine.chat.core.messages.MessageComponent
import org.qbrp.engine.chat.core.system.ChatGroup
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.LoadPriority
import org.qbrp.system.utils.format.Format.asMiniMessage

@Autoload(LoadPriority.ADDON)
class Whisper: ChatAddon("whisper"), ServerModCommand {
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
        CommandsRepository.add(this)
    }

    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val node = dispatcher.register(CommandManager.literal("whisper")
            .then(CommandManager.argument("text", StringArgumentType.greedyString())
                .executes() { ctx ->
                    try {
                        val author = PlayerManager.getPlayerSession(requirePlayer(ctx) ?: return@executes 0)
                        val target = author.getLookingAt() ?: run {
                            ctx.source.sendMessage("<red>Вы не смотрите на игрока.".asMiniMessage())
                            return@executes 0
                        }
                        val message = ChatMessage(author.name, StringArgumentType.getString(ctx, "text"))
                        ChatGroups.handle(message, Engine.getAPI<ChatGroupsAPI>()?.getGroup("whisper") ?: get<ChatGroup>())

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