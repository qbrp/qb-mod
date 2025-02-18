package org.qbrp.core.game.commands.templates

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.server.command.ServerCommandSource
import java.util.concurrent.CompletableFuture

open class ListProvider<T>(
    private val listSupplier: () -> List<T>
) : SuggestionProvider<ServerCommandSource> {
    override fun getSuggestions(
        context: CommandContext<ServerCommandSource>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        val input = builder.input.substring(builder.start).lowercase()

        listSupplier().map { item ->
            val suggestion = if (item is String) "\"$item\"" else item.toString()
            suggestion
        }
            .filter { it.lowercase().contains(input) } // Фильтруем по вводу
            .sortedBy { it.lowercase().indexOf(input) } // Сортируем по индексу вхождения
            .forEach { builder.suggest(it) }

        return builder.buildFuture()
    }
}
