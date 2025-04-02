package org.qbrp.engine.music.plasmo.model.audio

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.core.game.commands.templates.ListProvider
import org.qbrp.engine.Engine
import org.qbrp.engine.music.MusicManagerAPI
import org.qbrp.engine.music.MusicManagerModule
import java.util.concurrent.CompletableFuture

class TracksSuggestionProvider() : ListProvider<String>({ Engine.getAPI<MusicManagerAPI>()?.getTracks()!!.map { it.name } })