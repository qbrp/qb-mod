package org.qbrp.main.engine.modules

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.main.engine.Engine
import java.util.concurrent.CompletableFuture

class ModuleSuggestionProvider : SuggestionProvider<ServerCommandSource> {
    override fun getSuggestions(
        context: CommandContext<ServerCommandSource>?,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        // Получаем уже введённую часть команды
        val remaining = builder.remaining.lowercase()

        // Фильтруем модули по префиксу
        Engine.modules
            .map { it.getName() }
            .filter { it.lowercase().startsWith(remaining) }
            .forEach { builder.suggest(it) }

        return builder.buildFuture()
    }
}
