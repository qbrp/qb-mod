package org.qbrp.plasmo.controller

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import org.imperial_hell.ihCommands.Format.toMinecraft
import org.qbrp.core.game.registry.ServerModCommand
import org.qbrp.plasmo.controller.view.LinkClickableButton
import org.qbrp.plasmo.controller.view.ViewCommands
import org.qbrp.plasmo.MusicStorage

class TrackCommands : ServerModCommand {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager.literal("tracks")
                .then(
                    CommandManager.literal("list")
                        .executes { executeWithExceptionHandling(it) { listTracks(it) } }
                ).then(
                    CommandManager.literal("add")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                            .then(CommandManager.argument("link", StringArgumentType.string())
                                .then(CommandManager.argument("cycles", IntegerArgumentType.integer())
                                    .executes { executeWithExceptionHandling(it) { addTrack(it) } }
                                )
                            )
                        )
                ).then(
                    CommandManager.literal("delete")
                        .then(CommandManager.literal("all")
                            .executes { executeWithExceptionHandling(it) { deleteAllTracks(it) } }
                        ).then(
                            CommandManager.argument("name", StringArgumentType.string())
                                .executes { executeWithExceptionHandling(it) { deleteTrack(it) } }
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

    private fun listTracks(context: CommandContext<ServerCommandSource>): Int {
        val source = context.source
        val tracks = MusicStorage.getAllTracks()
        if (tracks.isEmpty()) {
            source.sendMessage(Text.literal("Нет доступных треков."))
        } else {
            tracks.forEach { track ->
                val cycles = if (track.cycle != -1) "&2(${track.cycle}) " else " "
                source.sendMessage(Text.literal("")
                    .append("&a${track.name} ".toMinecraft())
                    .append("&7${ViewCommands.formatTime(track.startTimestamp.toInt())} - ${ViewCommands.formatTime(track.endTimestamp.toInt())} ".toMinecraft())
                    .append(cycles.toMinecraft())
                    .append("&7: ".toMinecraft())
                    .append(LinkClickableButton(track.link, 43520).toText()))
            }
        }
        return Command.SINGLE_SUCCESS
    }

    private fun addTrack(context: CommandContext<ServerCommandSource>): Int {
        val name = StringArgumentType.getString(context, "name")
        val link = StringArgumentType.getString(context, "link")
        val cycles = IntegerArgumentType.getInteger(context, "cycles")

        MusicStorage.addTrack(name, link, cycles)
        context.source.sendMessage(Text.literal("Трек \"$name\" успешно добавлен."))
        return Command.SINGLE_SUCCESS
    }

    private fun deleteAllTracks(context: CommandContext<ServerCommandSource>): Int {
        context.source.sendMessage(Text.literal("Все треки успешно удалены."))
        return Command.SINGLE_SUCCESS
    }

    private fun deleteTrack(context: CommandContext<ServerCommandSource>): Int {
        val track = StringArgumentType.getString(context, "name")
        if (!MusicStorage.isTrackExists(track)) {
            context.source.sendError(Text.literal("Трек \"$track\" не найден."));
        } else {
            MusicStorage.deleteTrack(track)
            context.source.sendMessage(Text.literal("Трек \"$track\" успешно удален."))
        }
        return Command.SINGLE_SUCCESS
    }
}
