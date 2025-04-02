package org.qbrp.engine.chat.addons

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundEvents
import org.koin.core.component.get
import org.koin.dsl.module
import org.qbrp.core.game.player.PlayerManager
import org.qbrp.core.game.registry.CommandsRepository
import org.qbrp.core.game.registry.ServerModCommand
import org.qbrp.core.resources.data.config.ConfigInitializationCallback
import org.qbrp.core.resources.data.config.ServerConfigData
import org.qbrp.engine.Engine
import org.qbrp.engine.chat.ChatAddon
import org.qbrp.engine.chat.ChatModule.Companion.SYSTEM_MESSAGE_AUTHOR
import org.qbrp.engine.chat.addons.groups.ChatGroupsAPI
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.engine.chat.core.system.ChatGroup
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.LoadPriority
import org.qbrp.system.utils.format.Format.asMiniMessage

@Autoload(LoadPriority.ADDON)
class PrivateMessages: ChatAddon("pms"), ServerModCommand {
    init {
        dependsOn { Engine.isApiAvailable<ChatGroupsAPI>() }
    }

    override fun load() {
        super.load()
        CommandsRepository.add(this)
        ConfigInitializationCallback.EVENT.register() {
            Engine.getAPI<ChatGroupsAPI>()!!.addGroup(get<PmChatGroup>())
        }
    }

    override fun getKoinModule() = module {
        single { PmChatGroup(get<ServerConfigData>().chat.commands.formatPm) }
    }

    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(CommandManager.literal("pm")
            .then(argument("player", EntityArgumentType.entities())
                .then(argument("text", StringArgumentType.greedyString())
                .executes() { ctx ->
                    val name = ctx.source.player?.name?.string ?: SYSTEM_MESSAGE_AUTHOR
                    val target = EntityArgumentType.getEntity(ctx, "player") as? ServerPlayerEntity
                        ?: run { ctx.source.sendError("<red>Выбранный получатель не является игроком.".asMiniMessage()); return@executes 1 }
                    val text = StringArgumentType.getString(ctx, "text")
                    val message = ChatMessage(name, text).apply {
                        getTagsBuilder()
                            .placeholder("recipientName", target.name.string)
                            .placeholder("recipientDisplayName", target.displayName.string)
                            .placeholder("recipientRpName",
                                PlayerManager.getPlayerSession(target.name.string)?.account?.displayName ?: target.name.string
                            )
                            .component("sound", SoundEvents.UI_BUTTON_CLICK.key.get().value.toString())
                            .component("mention", target.name.string)
                            .component("group", "pm")
                    }
                    chatAPI.handleMessage(message)
                    1
                })))
    }

    class PmChatGroup(format: String): ChatGroup("pm", radius = -1, format = format) {
        override fun getPlayersCanSee(source: PlayerEntity): List<PlayerEntity> {
            return emptyList()
        }
    }
}