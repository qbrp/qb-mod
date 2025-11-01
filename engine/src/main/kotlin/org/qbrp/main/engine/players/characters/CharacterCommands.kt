package org.qbrp.main.engine.players.characters

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.components.extensions.require
import org.qbrp.main.core.fabricmc.commands.ServerCommandDispatcher
import org.qbrp.main.core.fabricmc.commands.registerCommand
import org.qbrp.main.engine.players.characters.components.LookManager
import org.qbrp.main.engine.players.characters.components.CharactersBank
import org.qbrp.main.engine.players.model.service.PlayersUtil
import java.lang.NullPointerException
import java.util.concurrent.CompletableFuture

fun ServerCommandDispatcher.registerCharacterCommands() {
    register(
        CommandManager.literal("character")
            .then(
                CommandManager.argument("label", StringArgumentType.greedyString())
                    .suggests(CharactersSuggestionProvider()).executes { ctx ->
                        val session = PlayersUtil.getPlayerSession(ctx.source.player!!)
                        val characterName = StringArgumentType.getString(ctx, "label")
                        try {
                            val character = session.account.characters.find { it.name == (characterName) }
                            if (character != null) {
                                session.require<CharactersBank>().setCharacterOrBuild(character)
                            } else {
                                session.sendChatMessage("<red>Персонаж не найден")
                            }
                        } catch (e: Exception) {
                            if (e is NullPointerException) {
                                session.sendChatMessage("<red>Вы ещё не создали персонажа")
                            }
                        }
                        1
                    }
            )
    )
    register(
        CommandManager.literal("look")
            .then(CommandManager.argument("label", StringArgumentType.greedyString())
                    .suggests(LookSuggestionProvider()).executes { ctx ->
                        val session = PlayersUtil.getPlayerSession(ctx.source.player!!)
                        val lookName = StringArgumentType.getString(ctx, "label")
                        session.get<LookManager>()?.let {
                            try {
                                val success = it.updateLook(lookName)
                                session.sendChatMessage(if (success) "<gray>Применён облик ${it.getFormattedName()}" else "<red>Облик не найден")
                            } catch (e: Exception) {
                                if (e is NullPointerException) {
                                    session.sendChatMessage("<red>Вы ещё не создали персонажа")
                                }
                            }
                        }
                        1
                    }
            )
    )
}

class CharactersSuggestionProvider : SuggestionProvider<ServerCommandSource> {
    override fun getSuggestions(
        context: CommandContext<ServerCommandSource?>?,
        builder: SuggestionsBuilder?
    ): CompletableFuture<Suggestions?>? {
        val player = context?.source?.player
        if (player != null) {
            val session = PlayersUtil.getPlayerSession(player)
            val account = session.account
            account.characters.forEach {
                builder?.suggest(it.name)
            }
        }
        return builder?.buildFuture()
    }
}

class LookSuggestionProvider : SuggestionProvider<ServerCommandSource> {
    override fun getSuggestions(
        context: CommandContext<ServerCommandSource?>?,
        builder: SuggestionsBuilder?
    ): CompletableFuture<Suggestions?>? {
        val player = context?.source?.player
        if (player != null) {
            val session = PlayersUtil.getPlayerSession(player)
            val character = session.get<CharactersBank>()?.characterData
            character?.appearance?.looks?.forEach {
                builder?.suggest(it.name)
            }
        }
        return builder?.buildFuture()
    }
}