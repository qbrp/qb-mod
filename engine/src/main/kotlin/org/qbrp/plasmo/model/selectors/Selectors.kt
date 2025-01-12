package org.qbrp.plasmo.model.selectors

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.server.command.ServerCommandSource
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

object Selectors {
    private val selectorMap = mapOf(
        "player" to PlayerSelector::class,
        "group" to GroupSelector::class,
        "region" to RegionSelector::class
    )

    fun createSelector(name: String, params: List<String>): Selector? {
        return selectorMap[name]?.primaryConstructor?.call(params)
    }

    fun getSelectorName(selectorClass: KClass<out Selector>): String? {
        return selectorMap.entries.find { it.value == selectorClass }?.key
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
