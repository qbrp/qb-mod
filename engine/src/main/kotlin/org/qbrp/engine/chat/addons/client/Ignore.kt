package org.qbrp.engine.chat.addons.client

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.core.mc.registry.CommandsRepository
import org.qbrp.core.mc.registry.ServerModCommand
import org.qbrp.engine.Engine
import org.qbrp.engine.chat.ChatAPI
import org.qbrp.engine.chat.addons.groups.ChatGroupsAPI
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.LoadPriority
import org.qbrp.system.modules.QbModule
import org.qbrp.system.networking.messages.Message
import org.qbrp.system.networking.messages.Messages
import org.qbrp.system.networking.messages.types.Signal
import org.qbrp.system.networking.messages.types.StringContent
import org.qbrp.system.networking.messaging.NetworkManager
import java.util.concurrent.CompletableFuture

@Autoload(LoadPriority.ADDON)
class Ignore: QbModule("chat-addon-ignore"), ServerModCommand {
    init {
        dependsOn { Engine.isApiAvailable<ChatAPI>() }
    }

    override fun onLoad() {
        CommandsRepository.add(this)
    }

    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(CommandManager.literal("ignore")
            .then(CommandManager.argument("group", StringArgumentType.string())
                .suggests(GroupSuggestionProvider())
                .executes { ctx ->
                    NetworkManager.sendMessage(ctx.source.player!!,
                        Message(Messages.invokeCommand("ignore"),
                            StringContent(StringArgumentType.getString(ctx, "group"))))
                    1
                }
            )
        )
        dispatcher.register(CommandManager.literal("spy")
            .executes { ctx ->
                NetworkManager.sendMessage(ctx.source.player!!,
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