package org.qbrp.plasmo.model.selectors

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.server.command.ServerCommandSource
import java.util.concurrent.CompletableFuture

object Selectors {
    private val selectorMap = mapOf(
        "player" to { params: MutableMap<String, String> -> PlayerSelector(params["nickname"] ?: "") },
        "group" to { params: MutableMap<String, String> -> GroupSelector(params["name"] ?: "") },
        "region" to { params: MutableMap<String, String> -> RegionSelector(params["name"] ?: "") }
    )

    fun createSelector(name: String, params: MutableMap<String, String>): Selector? {
        return selectorMap[name]?.invoke(params)
    }

    class SelectorsProvider : SuggestionProvider<ServerCommandSource> {
        override fun getSuggestions(
            context: CommandContext<ServerCommandSource>,
            builder: SuggestionsBuilder
        ): CompletableFuture<Suggestions> {
            selectorMap.keys.forEach { builder.suggest(it) }
            return builder.buildFuture()
        }
    }


}