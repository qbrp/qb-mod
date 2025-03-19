package org.qbrp.engine.music.plasmo.model.selectors

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.engine.Engine
import org.qbrp.engine.music.plasmo.MusicStorage
import org.qbrp.engine.music.plasmo.model.priority.Priority
import java.util.concurrent.CompletableFuture

class PrioritySuggestionsProvider(
    private val storage: MusicStorage,
    private val list: () -> List<Priority> = { storage.priorities.getAll() }
) : SuggestionProvider<ServerCommandSource> {
    override fun getSuggestions(
        context: CommandContext<ServerCommandSource>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        list().forEach { priority ->
            builder.suggest(priority.name)
        }
        return builder.buildFuture()
    }
}
