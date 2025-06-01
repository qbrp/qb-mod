package org.qbrp.main.engine.music.plasmo.controller

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.qbrp.deprecated.CommandBuilder
import org.qbrp.deprecated.DependencyFabric
import org.qbrp.deprecated.Deps
import org.qbrp.deprecated.annotations.Arg
import org.qbrp.deprecated.annotations.Command
import org.qbrp.deprecated.annotations.Execute
import org.qbrp.deprecated.annotations.Provider
import org.qbrp.deprecated.annotations.SubCommand
import org.qbrp.main.core.mc.commands.CommandRegistryEntry
import org.qbrp.main.core.utils.format.Format.asMiniMessage
import org.qbrp.main.engine.music.plasmo.MusicStorage
import org.qbrp.main.engine.music.plasmo.view.LinkClickableButton
import org.qbrp.main.engine.music.plasmo.view.MusicViewCommand
import org.qbrp.main.engine.music.plasmo.model.audio.TracksSuggestionProvider
import org.qbrp.main.engine.music.plasmo.playback.lavaplayer.AudioManager
import org.qbrp.main.core.utils.format.Format.formatMinecraft

@Command("tracks")
class TrackCommand(val storage: MusicStorage): CommandRegistryEntry {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandBuilder()
                .importDependencies(
                    DependencyFabric()
                        .register("storage", storage)
                        .createDeps()
                )
                .buildTree(this::class.java)
                .getCommand()
                .getLiteral()
        )
    }


    @SubCommand
    class List {
        @Execute
        fun listTracks(context: CommandContext<ServerCommandSource>, deps: Deps) {
            val source = context.source
            val tracks = (deps.get("storage") as MusicStorage).getAllTracks()
            if (tracks.isEmpty()) {
                source.sendMessage("Нет доступных треков.".asMiniMessage())
            } else {
                tracks.forEach { track ->
                    val cycles = if (track.loops != -1) "&2(${track.loops}) " else " "
                    source.sendMessage(Text.literal("")
                        .append("&a${track.name} ".asMiniMessage())
                        .append("&7${MusicViewCommand.formatTime(track.startTimestamp.toInt())} - ${MusicViewCommand.formatTime(track.endTimestamp.toInt())} ".asMiniMessage())
                        .append(cycles.asMiniMessage())
                        .append("&7: ".asMiniMessage())
                        .append(LinkClickableButton(track.link, 43520).toText()))
                    }
            }

        }
    }

    @SubCommand
    class Add(@Arg("string") val name: String,
              @Arg("string") val link: String,
              @Arg("integer") val repeats: Int): KoinComponent {

        @Execute
        fun addTrack(context: CommandContext<ServerCommandSource>, deps: Deps) {
            try {
                AudioManager.getTrack(link)
                (deps.get("storage") as MusicStorage).createTrack(name, link, repeats)
                context.source.sendMessage(Text.literal("Трек \"$name\" успешно добавлен."))
            } catch (e: Exception) {
                context.source.sendError(Text.literal("Ошибка добавления \"$name\": ${e.message} "))
            }
        }

    }

    @SubCommand
    class Edit(@Arg("string") @Provider(TracksSuggestionProvider::class) val trackName: String) {

        @SubCommand
        class EndTimestamp(@Arg("string", true) val trackName: String,
                           @Arg("integer") val minutes: Int,
                           @Arg("integer") val seconds: Int): KoinComponent {

            @Execute
            fun edit(context: CommandContext<ServerCommandSource>, deps: Deps) {
                val storage = deps.get("storage") as MusicStorage
                val time = minutes * 60 + seconds
                storage.getTrackOrThrow(trackName)
                    .apply {endTimestamp = time.toDouble() }
                    .also { get<MusicStorage>().save(it) }
                context.source.sendMessage("Трек $trackName изменен!".asMiniMessage())
            }

        }

        @SubCommand
        class StartTimestamp(@Arg("string", true) val trackName: String,
                             @Arg("integer") val minutes: Int,
                             @Arg("integer") val seconds: Int): KoinComponent {

            @Execute
            fun edit(context: CommandContext<ServerCommandSource>, deps: Deps) {
                val storage = deps.get("storage") as MusicStorage
                val time = minutes * 60 + seconds
                storage.getTrackOrThrow(trackName)
                    .apply { startTimestamp = time.toDouble() }
                    .also { get<MusicStorage>().save(it) }
                context.source.sendMessage("Трек $trackName изменен!".asMiniMessage())
            }

        }

        @SubCommand
        class Repeats(@Arg("string", true) val trackName: String,
                      @Arg("integer") val repeats: Int): KoinComponent {

            @Execute
            fun edit(context: CommandContext<ServerCommandSource>, deps: Deps) {
                val storage = deps.get("storage") as MusicStorage
                storage.getTrackOrThrow(trackName)
                    .apply { loops = repeats }
                    .also { get<MusicStorage>().save(it) }
                context.source.sendMessage("Трек $trackName изменен!".asMiniMessage())
            }

        }

    }


    @SubCommand
    class Delete(@Arg("string") @Provider(TracksSuggestionProvider::class) val name: String) {

        @Execute
        fun deleteTrack(context: CommandContext<ServerCommandSource>, deps: Deps) {
            val storage = (deps.get("storage") as MusicStorage)
            if (!storage.isTrackExists(name)) {
                context.source.sendError(Text.literal("Трек \"$name\" не найден."));
            } else {
                storage.deleteTrack(name)
                context.source.sendMessage(Text.literal("Трек \"$name\" успешно удален."))
            }
        }

    }

}