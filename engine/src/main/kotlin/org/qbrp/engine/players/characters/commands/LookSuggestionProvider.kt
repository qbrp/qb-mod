package org.qbrp.engine.characters

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.core.mc.player.PlayerManager
import org.qbrp.engine.players.characters.Character
import java.util.concurrent.CompletableFuture

class LookSuggestionProvider(): SuggestionProvider<ServerCommandSource> {
    override fun getSuggestions(
        context: CommandContext<ServerCommandSource?>?,
        builder: SuggestionsBuilder?
    ): CompletableFuture<Suggestions?>? {
        val player = context?.source?.player
        if (player != null) {
            val session = PlayerManager.getPlayerSession(player)
            val character = session.state.getComponent<Character>()?.data
            character?.appearance?.looks?.forEach {
                builder?.suggest(it.name)
            }
        }
        return builder?.buildFuture()
    }
}