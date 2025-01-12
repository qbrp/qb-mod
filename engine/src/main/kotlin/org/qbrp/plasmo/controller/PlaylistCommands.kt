package org.qbrp.plasmo.controller

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import org.qbrp.core.game.registry.ServerModCommand
import org.qbrp.plasmo.model.audio.Playlist
import org.qbrp.plasmo.model.priority.Priorities
import org.qbrp.plasmo.model.priority.Priority
import org.qbrp.plasmo.model.selectors.Selector
import org.qbrp.plasmo.model.selectors.Selectors
import org.qbrp.plasmo.MusicStorage

class PlaylistCommands : ServerModCommand {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager.literal("playlists")
                .then(
                    CommandManager.literal("add")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                            .then(CommandManager.argument("selector", StringArgumentType.word())
                                .suggests(Selectors.SelectorsProvider())
                                .then(CommandManager.argument("selectorArgs", StringArgumentType.string())
                                    .then(CommandManager.argument("priority", StringArgumentType.word())
                                        .suggests(Priorities.prioritySuggestions)
                                        .executes { executeWithExceptionHandling(it) { playlistsAdd(it) } }
                                    )
                                )
                            )
                        )
                ).then(
                    CommandManager.literal("play")
                        .then(CommandManager.argument("playlistName", StringArgumentType.string())
                        .executes { executeWithExceptionHandling(it) { playlistPlay(it) } }
                    )
                ).then(
                    CommandManager.literal("stop")
                        .then(CommandManager.argument("playlistName", StringArgumentType.string())
                            .executes { executeWithExceptionHandling(it) { playlistStop(it) } }
                        )
                ).then(
                    CommandManager.literal("edit")
                        .then(CommandManager.argument("playlistName", StringArgumentType.string())
                            .then(
                                CommandManager.literal("queue")
                                    .then(
                                        CommandManager.literal("add")
                                            .then(CommandManager.argument("trackName", StringArgumentType.string())
                                                .executes { executeWithExceptionHandling(it) { addTrack(it) } }
                                            )
                                    )
                                    .then(
                                        CommandManager.literal("remove")
                                            .then(CommandManager.argument("trackNumber", IntegerArgumentType.integer())
                                                .executes { executeWithExceptionHandling(it) { queueRemove(it) } }
                                            )
                                    )
                                    .then(
                                        CommandManager.literal("move")
                                            .then(CommandManager.argument("fromIndex", IntegerArgumentType.integer())
                                                .then(CommandManager.argument("toIndex", IntegerArgumentType.integer())
                                                    .executes { executeWithExceptionHandling(it) { queueMove(it) } }
                                                )
                                            )
                                    )
                            ).then(
                                CommandManager.literal("settings")
                                    .then(
                                        CommandManager.literal("loops")
                                            .then(CommandManager.argument("cycle", IntegerArgumentType.integer())
                                                .executes { executeWithExceptionHandling(it) { setLoop(it) } }
                                            )
                                    )
                                    .then(
                                        CommandManager.literal("selector")
                                            .then(CommandManager.argument("selector", StringArgumentType.word())
                                                .suggests(Selectors.SelectorsProvider())
                                                .then(CommandManager.argument("selectorArgs", StringArgumentType.string())
                                                    .executes { executeWithExceptionHandling(it) { setSelector(it) } }
                                                )
                                            )
                                    )
                                    .then(
                                        CommandManager.literal("priority")
                                            .then(CommandManager.argument("priority", StringArgumentType.word())
                                                .suggests(Priorities.prioritySuggestions)
                                                .executes { executeWithExceptionHandling(it) { setPriority(it) } }
                                            )
                                    )
                            )
                        )
                )
        )
    }

    private fun <T> executeWithExceptionHandling(context: CommandContext<ServerCommandSource>, action: () -> T): Int {
        return try {
            action()
            Command.SINGLE_SUCCESS
        } catch (e: Exception) {
            context.source.sendError(Text.literal("Произошла ошибка: ${e.message}"))
            Command.SINGLE_SUCCESS
        }
    }

    private fun playlistStop(context: CommandContext<ServerCommandSource>): Int {
        val editor = PlaylistEditor(context).apply { getPlaylist() }
        (editor.playlist as Playlist).stop()
        return Command.SINGLE_SUCCESS
    }

    private fun playlistPlay(context: CommandContext<ServerCommandSource>): Int {
        val editor = PlaylistEditor(context).apply { getPlaylist() }
        (editor.playlist as Playlist).play()
        return Command.SINGLE_SUCCESS
    }


    private fun setLoop(context: CommandContext<ServerCommandSource>): Int {
        val editor = PlaylistEditor(context).apply { getPlaylist() }
        if (editor.playlist != null) {
            editor.playlist!!.cycle = IntegerArgumentType.getInteger(context, "cycle")
            editor.feedback("Плейлист успешно обновлен.")
        }
        return Command.SINGLE_SUCCESS
    }

    private fun setSelector(context: CommandContext<ServerCommandSource>): Int {
        val editor = PlaylistEditor(context).apply { getPlaylist(); getSelector() }
        if (editor.playlist != null && editor.selector != null) {
            editor.playlist!!.selector = editor.selector as Selector
            editor.feedback("Плейлист успешно обновлен.")
        }
        return Command.SINGLE_SUCCESS
    }

    private fun setPriority(context: CommandContext<ServerCommandSource>): Int {
        val editor = PlaylistEditor(context).apply { getPlaylist(); getPriority() }
        if (editor.playlist != null && editor.priority != null) {
            editor.playlist!!.priority = editor.priority as Priority
            editor.feedback("Плейлист успешно обновлен.")
        }
        return Command.SINGLE_SUCCESS
    }

    private fun playlistsAdd(context: CommandContext<ServerCommandSource>): Int {
        val editor = PlaylistEditor(context).apply { getPriority(); getSelector() }
        MusicStorage.addPlaylist(StringArgumentType.getString(context, "name"), editor.selector!!, editor.priority!!)
        editor.playlist?.play()
        editor.feedback("Плейлист успешно добавлен.")
        return Command.SINGLE_SUCCESS
    }

    private fun addTrack(context: CommandContext<ServerCommandSource>): Int {
        val editor = PlaylistEditor(context).apply { getPlaylist(); getTrack() }
        if (editor.playlist != null && editor.track != null) {
            editor.playlist!!.addTrack(editor.track!!.name)
            editor.feedback("Трек добавлен в плейлист.")
        }
        return Command.SINGLE_SUCCESS
    }

    private fun queueRemove(context: CommandContext<ServerCommandSource>): Int {
        val editor = PlaylistEditor(context).apply { getPlaylist() }
        val trackNumber = IntegerArgumentType.getInteger(context, "trackNumber")
        if (editor.playlist != null) {
            editor.playlist!!.removeTrack(trackNumber)
            editor.feedback("Трек успешно убран.")
        }
        return Command.SINGLE_SUCCESS
    }

    private fun queueMove(context: CommandContext<ServerCommandSource>): Int {
        val editor = PlaylistEditor(context).apply { getPlaylist() }
        val fromIndex = IntegerArgumentType.getInteger(context, "fromIndex")
        val toIndex = IntegerArgumentType.getInteger(context, "toIndex")
        if (editor.playlist != null) {
            editor.playlist!!.moveTrack(fromIndex, toIndex)
            editor.feedback("Трек успешно перемещен.")
        }
        return Command.SINGLE_SUCCESS
    }
}
