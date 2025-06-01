package org.qbrp.main.engine.music.plasmo.model.selectors

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.server.command.ServerCommandSource
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass

class SelectorsProvider(val selectorsMap: Map<String, KClass<*>> = SelectorBuilder.selectorMap) : SuggestionProvider<ServerCommandSource> {
    override fun getSuggestions(
        context: CommandContext<ServerCommandSource>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        selectorsMap.keys.forEach { builder.suggest(it) }
        return builder.buildFuture()
    }
}