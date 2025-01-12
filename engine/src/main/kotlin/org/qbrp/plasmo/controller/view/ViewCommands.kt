package org.qbrp.plasmo.controller.view

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import org.qbrp.core.game.registry.ServerModCommand
import org.qbrp.plasmo.playback.PlayerSelectionHandler

class ViewCommands(val selection: PlayerSelectionHandler): ServerModCommand {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager.literal("music")
                    .executes { context -> execute(context) }
        )
    }

    fun execute(context: CommandContext<ServerCommandSource>): Int {
        val player = context.source.player as ServerPlayerEntity
        val playlists = selection.getSelectedPlaylists(player)
        if (playlists.isEmpty()) {
            player.sendMessage(Text.literal("Плейлисты не найдены."))
        } else {
            playlists.forEach { playlist ->
                try {
                    player.sendMessage(playlist.getView().getText())
                } catch (e: Exception) {
                    println(e.message)
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    companion object {
        fun formatTime(time: Int): String {
            val minutes = time / 60
            val seconds = time % 60
            return String.format("%02d:%02d", minutes, seconds)
        }
    }
}