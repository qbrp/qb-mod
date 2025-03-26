package org.qbrp.engine.chat.addons

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.command.suggestion.SuggestionProviders
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundEvents
import org.koin.core.component.get
import org.qbrp.core.game.player.PlayerManager
import org.qbrp.core.game.registry.ServerModCommand
import org.qbrp.core.resources.data.config.ServerConfigData
import org.qbrp.engine.Engine
import org.qbrp.engine.chat.ChatAddon
import org.qbrp.engine.chat.ChatModule.Companion.SYSTEM_MESSAGE_AUTHOR
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.LoadPriority
import org.qbrp.system.utils.format.Format.asMiniMessage

@Autoload(LoadPriority.ADDON)
class PrivateMessages: ChatAddon("pms"), ServerModCommand {
    override fun load() {
        super.load()
    }

    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(CommandManager.literal("pm")
            .then(argument("player", EntityArgumentType.entities())
                .executes() { ctx ->
                    val name = ctx.source.player?.name?.string ?: SYSTEM_MESSAGE_AUTHOR
                    val target = EntityArgumentType.getEntity(ctx, "player") as? ServerPlayerEntity
                        ?: run { ctx.source.sendError("<red>Выбранный получатель не является игроком.".asMiniMessage()); return@executes 1 }
                    val message = ChatMessage(name, get<ServerConfigData>().chat.commands.formatPm).apply {
                        getTagsBuilder()
                            .placeholder("recipientName", target.name.string)
                            .placeholder("recipientName", target.displayName.string)
                            .placeholder("recipientRpName",
                                PlayerManager.getPlayerData(target.name.string)?.account?.displayName
                                    ?: run { ctx.source.sendError("<red>Получатель не зарегистрировал аккаунт.".asMiniMessage()); return@executes 1 }
                            )
                            .component("sound", SoundEvents.UI_BUTTON_CLICK.key.get().value.toString())
                    }
                    chatAPI.sendMessage(target, message)
                    1
                }))
    }
}