package org.qbrp.main.engine.chat.addons

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundEvents
import org.qbrp.main.core.mc.commands.CommandsModule
import org.qbrp.main.core.mc.commands.CommandRegistryEntry
import org.qbrp.deprecated.resources.data.config.ConfigInitializationCallback
import org.qbrp.deprecated.resources.data.config.ServerConfigData
import org.qbrp.main.core.mc.player.PlayersUtil
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.chat.ChatAddon
import org.qbrp.main.engine.chat.ChatModule.Companion.SYSTEM_MESSAGE_AUTHOR
import org.qbrp.main.engine.chat.addons.groups.ChatGroupsAPI
import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.engine.chat.core.system.ChatGroup
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.LoadPriority
import org.qbrp.main.core.utils.format.Format.asMiniMessage
import org.koin.core.component.get
import org.qbrp.main.core.mc.commands.CommandsAPI

@Autoload(LoadPriority.ADDON)
class PrivateMessages: ChatAddon("pms"), CommandRegistryEntry {
    init {
        dependsOn { Engine.isApiAvailable<ChatGroupsAPI>() }
    }

    override fun onLoad() {
        super.onLoad()
        get<CommandsAPI>().add(this)
        ConfigInitializationCallback.EVENT.register() {
            Engine.getAPI<ChatGroupsAPI>()!!.addGroup(getLocal<PmChatGroup>())
        }
    }

    override fun getKoinModule() = inner {
        scoped { PmChatGroup(get<ServerConfigData>().chat.commands.formatPm) }
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
                                PlayersUtil.getPlayerSession(target.name.string)?.displayName ?: target.name.string
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