package org.qbrp.main.engine.music.plasmo.controller

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.deprecated.CommandBuilder
import org.qbrp.deprecated.DependencyFabric
import org.qbrp.deprecated.Deps
import org.qbrp.deprecated.annotations.Arg
import org.qbrp.deprecated.annotations.Command
import org.qbrp.deprecated.annotations.Execute
import org.qbrp.deprecated.annotations.Provider
import org.qbrp.deprecated.annotations.SubCommand
import org.qbrp.main.core.fabricmc.commands.CommandRegistryEntry
import org.qbrp.main.engine.music.plasmo.MusicStorage
import org.qbrp.main.engine.music.plasmo.model.audio.TracksSuggestionProvider
import org.qbrp.main.engine.music.plasmo.model.priority.Priorities
import org.qbrp.main.engine.music.plasmo.model.priority.Priority
import org.qbrp.main.engine.music.plasmo.model.selectors.Selector
import org.qbrp.main.engine.music.plasmo.model.selectors.SelectorBuilder
import org.qbrp.main.engine.music.plasmo.model.selectors.SelectorsProvider
import org.qbrp.main.core.util.format.Format.formatMm
import org.qbrp.main.core.util.runAsyncCommand

@Command(name = "playlists")
class PlaylistCommand(val priorities: Priorities, val storage: MusicStorage) : CommandRegistryEntry {

    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandBuilder()
                .importDependencies(
                    DependencyFabric()
                        .register("priorities", priorities)
                        .register("storage", storage)
                        .createDeps()
                )
                .printErrors()
                .buildTree(this::class.java)
                .getCommand()
                .getLiteral()
        )
    }

    @SubCommand("add")
    class Add(
        @Arg("string") val name: String,
    ) {

        @SubCommand
        class Default(
            @Arg("string", true) val name: String,
            @Arg("string") @Provider(SelectorsProvider::class) val selector: String,
            @Arg("string") val selectorArgs: String,
            @Arg("string") val priority: String
        ) {
            @Execute
            fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
                try {
                    val selectorInstance = SelectorBuilder().createSelector(selector, selectorArgs)
                    val priorityInstance = (deps.get("priorities") as Priorities).getPriority(priority)
                    (deps.get("storage") as MusicStorage).addPlaylist(
                        name,
                        selectorInstance as Selector,
                        priorityInstance as Priority
                    )
                    context.source.sendMessage("Плейлист $name успешно добавлен!".formatMm())
                } catch (e: Exception) {
                    e.printStackTrace()
                    context.source.sendError("Ошибка: ${e.message}".formatMm())
                }
            }
        }

        @SubCommand
        class Shadow(
            @Arg("string", true) val name: String,
            @Arg("string") val originalName: String,
            @Arg("string") @Provider(SelectorsProvider::class) val selector: String,
            @Arg("string") val selectorArgs: String,
            @Arg("string") val priority: String
        ) {
            @Execute
            fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
                try {
                    val selectorInstance = SelectorBuilder().createSelector(selector, selectorArgs)
                    val priorityInstance = (deps.get("priorities") as Priorities).getPriority(priority)
                    (deps.get("storage") as MusicStorage).addShadow(
                        originalName,
                        name,
                        selectorInstance as Selector,
                        priorityInstance as Priority
                    )
                    context.source.sendMessage("Плейлист-копия $name успешно добавлен!".formatMm())
                } catch (e: Exception) {
                    e.printStackTrace()
                    context.source.sendError("Ошибка: ${e.message}".formatMm())
                }
            }
        }
    }

    @SubCommand
    class Delete(
        @Arg("string") val name: String
    ) {
        @Execute
        fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
            try {
                (deps.get("storage") as MusicStorage).deletePlayable(name)
                context.source.sendMessage("Плейлист $name архивирован.".formatMm())
            } catch (e: Exception) {
                e.printStackTrace()
                context.source.sendError("Ошибка: ${e.message}".formatMm())
            }
        }
    }

    @SubCommand("resume")
    class Play(@Arg("string") val playlistName: String) {
        @Execute
        fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
            try {
                val playlist = (deps.get("storage") as MusicStorage).getPlayable(playlistName)
                playlist?.sessionManager?.doForSessions { p, session -> session.radio?.resume() }
                context.source.sendMessage("Плейлист $playlistName запущен.".formatMm())
            } catch (e: Exception) {
                e.printStackTrace()
                context.source.sendError("Ошибка: ${e.message}".formatMm())
            }
        }
    }

    @SubCommand("stop")
    class Stop(@Arg("string") val playlistName: String) {
        @Execute
        fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
            try {
                val playlist = (deps.get("storage") as MusicStorage).getPlayable(playlistName)
                playlist?.sessionManager?.doForSessions { p, session -> session.radio?.stop() }
                context.source.sendMessage("Плейлист $playlistName остановлен.".formatMm())
            } catch (e: Exception) {
                context.source.sendError("Ошибка: ${e.message}".formatMm())
            }
        }
    }

    @SubCommand("edit")
    class Edit(@Arg("string") val playlistName: String) {

        @SubCommand("queue")
        class Queue {

            @SubCommand("add")
            class AddTrack(
                @Arg("string", true) val playlistName: String,
                @Arg("string") @Provider(TracksSuggestionProvider::class) val trackName: String) {

                @Execute
                fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
                    try {
                        if ((deps.get("storage") as MusicStorage).isTrackExists(trackName)) {
                            val playlist = (deps.get("storage") as MusicStorage).getPlayable(playlistName)
                            playlist?.queue?.addTrack(trackName)
                            playlist?.onUpdate()
                            context.source.sendMessage("Трек $trackName добавлен в плейлист.".formatMm())
                        } else {
                            context.source.sendMessage("Трек $trackName не существует.".formatMm())
                        }
                    } catch (e: Exception) {
                        context.source.sendError("Ошибка: ${e.message}".formatMm())
                    }
                }
            }

            @SubCommand("remove")
            class RemoveTrack(
                @Arg("string", true) val playlistName: String,
                @Arg("integer") val trackNumber: Int) {

                @Execute
                fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
                    try {
                        val playlist = (deps.get("storage") as MusicStorage).getPlayable(playlistName)
                        playlist?.queue?.removeTrack(trackNumber)
                        playlist?.onUpdate()
                        context.source.sendMessage("Трек номер $trackNumber удалён из плейлиста.".formatMm())
                    } catch (e: Exception) {
                        context.source.sendError("Ошибка: ${e.message}".formatMm())
                    }
                }
            }

            @SubCommand("move")
            class MoveTrack(
                @Arg("string", true) val playlistName: String,
                @Arg("integer") val fromIndex: Int,
                @Arg("integer") val toIndex: Int) {

                @Execute
                fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
                    try {
                        val playlist = (deps.get("storage") as MusicStorage).getPlayable(playlistName)
                        playlist?.sessionManager?.doForSessions { p, session -> session.queue.moveTo(fromIndex, toIndex) }
                        context.source.sendMessage("Трек был перемещен.".formatMm())
                    } catch (e: Exception) {
                        context.source.sendError("Ошибка: ${e.message}".formatMm())
                    }
                }
            }

            @SubCommand("moveUnary")
            class MoveTrackUnary(
                @Arg("string", true) val playlistName: String,
                @Arg("integer") val operation: Int) {

                @Execute
                fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
                    try {
                        val playlist = (deps.get("storage") as MusicStorage).getPlayable(playlistName)
                        playlist?.sessionManager?.doForSessions { p, session -> session.queue.moveTo(session.queue.currentTrackIndex, session.queue.currentTrackIndex + operation) }
                        context.source.sendMessage("Трек был перемещен.".formatMm())
                    } catch (e: Exception) {
                        context.source.sendError("Ошибка: ${e.message}".formatMm())
                    }
                }
            }

        }

        @SubCommand
        class Playback {

            @SubCommand("pos")
            class SetPlaybackPos(
                @Arg("string", true) val playlistName: String,
                @Arg("integer") val pos: Int) {

                @Execute
                fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
                    try {
                        val playlist = (deps.get("storage") as MusicStorage).getPlayable(playlistName)
                        runAsyncCommand(context,
                            operation = {
                                playlist?.sessionManager?.doForSessionsAsync { p, session ->
                                    session.queue.setPosition(
                                        pos
                                    ); session.createRadio()
                                }
                            },
                            callback = { context.source.sendMessage("Трек был перемещен.".formatMm()) }
                        )
                    } catch (e: Exception) {
                        context.source.sendError("Ошибка: ${e.message}".formatMm())
                    }
                }
            }

            @SubCommand("enabled")
            class SetEnabled(
                @Arg("string", true) val playlistName: String,
                @Arg("boolean") val enabled: Boolean) {

                @Execute
                fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
                    try {
                        val playlist = (deps.get("storage") as MusicStorage).getPlayable(playlistName)
                        if (enabled) { playlist?.enable(); playlist?.isManuallyDisabled = false; context.source.sendMessage("Плейлист включен.".formatMm()) }
                        if (!enabled) { playlist?.disable(); playlist?.isManuallyDisabled = true; context.source.sendMessage("Плейлист выключен.".formatMm()) }
                    } catch (e: Exception) {
                        context.source.sendError("Ошибка: ${e.message}".formatMm())
                    }
                }
            }

        }

        @SubCommand("settings")
        class Settings {

            @SubCommand("loops")
            class SetLoops(
                @Arg("string", true) val playlistName: String,
                @Arg("integer") val cycle: Int) {

                @Execute
                fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
                    try {
                        val playlist = (deps.get("storage") as MusicStorage).getPlayable(playlistName)
                        playlist?.queue?.repeats = cycle
                        playlist?.onUpdate()
                        context.source.sendMessage("Циклы плейлиста обновлены.".formatMm())
                    } catch (e: Exception) {
                        context.source.sendError("Ошибка: ${e.message}".formatMm())
                    }
                }
            }

            @SubCommand("selector")
            class SetSelector(@Arg("string", true) val playlistName: String,
                @Provider(SelectorsProvider::class) @Arg("string") val selectorName: String, @Arg("string") val selectorArgs: String) {

                @Execute
                fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
                    try {
                        val playlist = (deps.get("storage") as MusicStorage).getPlayable(playlistName)
                        val selector = SelectorBuilder().createSelector(selectorName, selectorArgs) as Selector
                        playlist?.selector = selector
                        playlist?.onUpdate()
                        context.source.sendMessage("Селектор обновлён.".formatMm())
                    } catch (e: Exception) {
                        context.source.sendError("Ошибка: ${e.message}".formatMm())
                    }
                }
            }

            @SubCommand("priority")
            class SetPriority(@Arg("string", true) val playlistName: String, @Arg("string") val priority: String) {
                @Execute
                fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
                    try {
                        val playlist = (deps.get("storage") as MusicStorage).getPlayable(playlistName)
                        val priority = (deps.get("priorities") as Priorities).getPriority(priority) as Priority
                        playlist?.priority = priority
                        playlist?.onUpdate()
                        context.source.sendMessage("Приоритет обновлён.".formatMm())
                    } catch (e: Exception) {
                        context.source.sendError("Ошибка: ${e.message}".formatMm())
                    }
                }
            }

            @SubCommand("label")
            class SetName(@Arg("string", true) val playlistName: String,
                          @Arg("string") val newName: String) {
                @Execute
                fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
                    try {
                        val playlist = (deps.get("storage") as MusicStorage).getPlayable(playlistName)
                        (deps.get("storage") as MusicStorage).changePlayableName(playlistName, newName)
                        playlist?.onUpdate()
                        context.source.sendMessage("Название обновлено.".formatMm())
                    } catch (e: Exception) {
                        context.source.sendError("Ошибка: ${e.message}".formatMm())
                    }
                }
            }
        }
    }
}
