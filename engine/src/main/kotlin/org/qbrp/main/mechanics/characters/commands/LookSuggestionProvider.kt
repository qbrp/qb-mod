package org.qbrp.main.engine.characters

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.engine.players.model.service.PlayersUtil
import org.qbrp.main.engine.players.characters.components.CharactersBank
import java.util.concurrent.CompletableFuture

class LookSuggestionProvider(): SuggestionProvider<ServerCommandSource> {
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