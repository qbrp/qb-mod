package org.qbrp.engine.music.plasmo.model.audio

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.engine.Engine
import java.util.concurrent.CompletableFuture

class TracksSuggestionProvider() : SuggestionProvider<ServerCommandSource> {
    override fun getSuggestions(
        context: CommandContext<ServerCommandSource>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        //Engine.musicManagerModule.storage.getAllTracks().forEach { track ->
            //builder.suggest(""""${track.name}"""")
        //}
        return builder.buildFuture()
    }
}