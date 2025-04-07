package org.qbrp.engine.characters

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.core.game.player.Account
import org.qbrp.core.game.player.PlayerManager
import java.util.concurrent.CompletableFuture

class CharactersSuggestionProvider(): SuggestionProvider<ServerCommandSource> {

    override fun getSuggestions(
        context: CommandContext<ServerCommandSource?>?,
        builder: SuggestionsBuilder?
    ): CompletableFuture<Suggestions?>? {
        val player = context?.source?.player
        if (player != null) {
            val session = PlayerManager.getPlayerSession(player)
            val account = if (session.isAuthorized()) session.account else null
            account?.characters?.forEach {
                builder?.suggest(it.name)
            }
        }
        return builder?.buildFuture()
    }
}