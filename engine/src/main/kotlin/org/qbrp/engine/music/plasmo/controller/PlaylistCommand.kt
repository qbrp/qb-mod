package org.qbrp.engine.music.plasmo.controller

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.core.game.registry.ServerModCommand
import org.qbrp.core.game.commands.CommandBuilder
import org.qbrp.core.game.commands.DependencyFabric
import org.qbrp.core.game.commands.Deps
import org.qbrp.core.game.commands.annotations.Arg
import org.qbrp.core.game.commands.annotations.Command
import org.qbrp.core.game.commands.annotations.Execute
import org.qbrp.core.game.commands.annotations.Provider
import org.qbrp.core.game.commands.annotations.SubCommand
import org.qbrp.engine.music.plasmo.MusicStorage
import org.qbrp.engine.music.plasmo.model.audio.TracksSuggestionProvider
import org.qbrp.engine.music.plasmo.model.priority.Priorities
import org.qbrp.engine.music.plasmo.model.priority.Priority
import org.qbrp.engine.music.plasmo.model.selectors.PrioritySuggestionsProvider
import org.qbrp.engine.music.plasmo.model.selectors.Selector
import org.qbrp.engine.music.plasmo.model.selectors.SelectorBuilder
import org.qbrp.engine.music.plasmo.model.selectors.SelectorsProvider
import org.qbrp.system.utils.format.Format.formatMinecraft

@Command(name = "playlists")
class PlaylistCommand(val priorities: Priorities, val storage: MusicStorage) : ServerModCommand {

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
        @Arg("string") @Provider(SelectorsProvider::class) val selector: String,
        @Arg("string") val selectorArgs: String,
        @Arg("string") @Provider(PrioritySuggestionsProvider::class) val priority: String
    ) {
        @Execute
        fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
            try {
                val selectorInstance = SelectorBuilder().createSelector(selector, selectorArgs)
                val priorityInstance = (deps.get("priorities") as Priorities).getPriority(priority)
                val playlist = (deps.get("storage") as MusicStorage).addPlaylist(name, selectorInstance as Selector, priorityInstance as Priority)

                playlist.queue.play()
                context.source.sendMessage("Плейлист $name успешно добавлен!".formatMinecraft())
            } catch (e: Exception) {
                e.printStackTrace()
                context.source.sendError("Ошибка: ${e.message}".formatMinecraft())
            }
        }
    }

    class Delete(
        @Arg("string") val name: String
    ) {
        @Execute
        fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
            try {
                (deps.get("storage") as MusicStorage).deletePlayable(name)
                context.source.sendMessage("Плейлист $name архивирован.".formatMinecraft())
            } catch (e: Exception) {
                e.printStackTrace()
                context.source.sendError("Ошибка: ${e.message}".formatMinecraft())
            }
        }
    }

    @SubCommand("play")
    class Play(@Arg("string") val playlistName: String) {
        @Execute
        fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
            try {
                val playlist = (deps.get("storage") as MusicStorage).getPlayable(playlistName)
                playlist?.queue?.play()
                context.source.sendMessage("Плейлист $playlistName запущен.".formatMinecraft())
            } catch (e: Exception) {
                e.printStackTrace()
                context.source.sendError("Ошибка: ${e.message}".formatMinecraft())
            }
        }
    }

    @SubCommand("stop")
    class Stop(@Arg("string") val playlistName: String) {
        @Execute
        fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
            try {
                val playlist = (deps.get("storage") as MusicStorage).getPlayable(playlistName)
                playlist?.queue?.radio?.stop()
                context.source.sendMessage("Плейлист $playlistName остановлен.".formatMinecraft())
            } catch (e: Exception) {
                context.source.sendError("Ошибка: ${e.message}".formatMinecraft())
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
                            context.source.sendMessage("Трек $trackName добавлен в плейлист.".formatMinecraft())
                        } else {
                            context.source.sendMessage("Трек $trackName не существует.".formatMinecraft())
                        }
                    } catch (e: Exception) {
                        context.source.sendError("Ошибка: ${e.message}".formatMinecraft())
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
                        context.source.sendMessage("Трек номер $trackNumber удалён из плейлиста.".formatMinecraft())
                    } catch (e: Exception) {
                        context.source.sendError("Ошибка: ${e.message}".formatMinecraft())
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
                        playlist?.queue?.moveTo(fromIndex, toIndex)
                        context.source.sendMessage("Трек был перемещен.".formatMinecraft())
                    } catch (e: Exception) {
                        context.source.sendError("Ошибка: ${e.message}".formatMinecraft())
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
                        playlist?.queue?.setPosition(pos)
                        context.source.sendMessage("Трек был перемещен.".formatMinecraft())
                    } catch (e: Exception) {
                        context.source.sendError("Ошибка: ${e.message}".formatMinecraft())
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
                        if (enabled) { playlist?.enable(); playlist?.isManuallyDisabled = false; context.source.sendMessage("Плейлист включен.".formatMinecraft()) }
                        if (!enabled) { playlist?.disable(); playlist?.isManuallyDisabled = true; context.source.sendMessage("Плейлист выключен.".formatMinecraft()) }
                    } catch (e: Exception) {
                        context.source.sendError("Ошибка: ${e.message}".formatMinecraft())
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
                        context.source.sendMessage("Циклы плейлиста обновлены.".formatMinecraft())
                    } catch (e: Exception) {
                        context.source.sendError("Ошибка: ${e.message}".formatMinecraft())
                    }
                }
            }

            @SubCommand("selector")
            class SetSelector(@Arg("string", true) val playlistName: String,
                @Provider(SelectorsProvider::class) @Arg("string") val selector: String, @Arg("string") val selectorArgs: String) {

                @Execute
                fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
                    try {
                        val playlist = (deps.get("storage") as MusicStorage).getPlayable(playlistName)
                        playlist?.selector = SelectorBuilder().createSelector(selector, selectorArgs) as Selector
                        context.source.sendMessage("Селектор обновлён.".formatMinecraft())
                    } catch (e: Exception) {
                        context.source.sendError("Ошибка: ${e.message}".formatMinecraft())
                    }
                }
            }

            @SubCommand("priority")
            class SetPriority(@Arg("string", true) val playlistName: String, @Arg("string") @Provider(PrioritySuggestionsProvider::class)  val priority: String) {
                @Execute
                fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
                    try {
                        val playlist = (deps.get("storage") as MusicStorage).getPlayable(playlistName)
                        playlist?.priority = (deps.get("priorities") as Priorities).getPriority(priority) as Priority
                        context.source.sendMessage("Приоритет обновлён.".formatMinecraft())
                    } catch (e: Exception) {
                        context.source.sendError("Ошибка: ${e.message}".formatMinecraft())
                    }
                }
            }

            @SubCommand("name")
            class SetName(@Arg("string", true) val playlistName: String,
                          @Arg("string") val newName: String) {
                @Execute
                fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
                    try {
                        val playlist = (deps.get("storage") as MusicStorage).getPlayable(playlistName)
                        (deps.get("storage") as MusicStorage).changePlayableName(playlistName, newName)
                        context.source.sendMessage("Название обновлено.".formatMinecraft())
                    } catch (e: Exception) {
                        context.source.sendError("Ошибка: ${e.message}".formatMinecraft())
                    }
                }
            }
        }
    }
}
