package org.qbrp.main.engine.chat.addons.client

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.main.core.mc.commands.CommandsModule
import org.qbrp.main.core.mc.commands.CommandRegistryEntry
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.chat.ChatAPI
import org.qbrp.main.engine.chat.addons.groups.ChatGroupsAPI
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.LoadPriority
import org.qbrp.main.core.modules.QbModule
import org.qbrp.main.core.utils.networking.messages.Message
import org.qbrp.main.core.utils.networking.messages.Messages
import org.qbrp.main.core.utils.networking.messages.types.Signal
import org.qbrp.main.core.utils.networking.messages.types.StringContent
import org.qbrp.main.core.utils.networking.messaging.NetworkUtil
import java.util.concurrent.CompletableFuture
import org.koin.core.component.get
import org.qbrp.main.core.mc.commands.CommandsAPI

@Autoload(LoadPriority.ADDON)
class Ignore: QbModule("chat-addon-ignore"), CommandRegistryEntry {
    init {
        dependsOn { Engine.isApiAvailable<ChatAPI>() }
    }

    override fun onLoad() {
        get<CommandsAPI>().add(this)
    }

    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(CommandManager.literal("ignore")
            .then(CommandManager.argument("group", StringArgumentType.string())
                .suggests(GroupSuggestionProvider())
                .executes { ctx ->
                    NetworkUtil.sendMessage(ctx.source.player!!,
                        Message(Messages.invokeCommand("ignore"),
                            StringContent(StringArgumentType.getString(ctx, "group"))))
                    1
                }
            )
        )
        dispatcher.register(CommandManager.literal("spy")
            .executes { ctx ->
                NetworkUtil.sendMessage(ctx.source.player!!,
                    Message(Messages.invokeCommand("spy"), Signal()))
                1
            }
        )
    }

    class GroupSuggestionProvider: SuggestionProvider<ServerCommandSource> {
        override fun getSuggestions(
            context: CommandContext<ServerCommandSource?>?,
            builder: SuggestionsBuilder?
        ): CompletableFuture<Suggestions?>? {
            Engine.getAPI<ChatGroupsAPI>()!!.getStorage().getAllGroups().forEach { group ->
                builder?.suggest(group.name)
            }
            return builder?.buildFuture()
        }
    }
}