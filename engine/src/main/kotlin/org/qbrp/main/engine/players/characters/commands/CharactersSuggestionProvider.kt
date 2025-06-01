package org.qbrp.main.engine.characters

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.main.core.mc.player.PlayersModule
import org.qbrp.main.core.mc.player.PlayersUtil
import java.util.concurrent.CompletableFuture

class CharactersSuggestionProvider(): SuggestionProvider<ServerCommandSource> {

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