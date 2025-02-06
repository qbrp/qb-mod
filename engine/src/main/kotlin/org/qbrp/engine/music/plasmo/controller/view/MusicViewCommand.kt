package org.qbrp.engine.music.plasmo.controller.view

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import org.qbrp.core.game.registry.ServerModCommand
import org.qbrp.core.game.commands.CommandBuilder
import org.qbrp.core.game.commands.DependencyFabric
import org.qbrp.core.game.commands.Deps
import org.qbrp.core.game.commands.annotations.Command
import org.qbrp.core.game.commands.annotations.Execute
import org.qbrp.engine.music.plasmo.playback.PlayerSelectionHandler
import org.qbrp.system.utils.format.Format.formatMinecraft

class MusicViewCommand(val selection: PlayerSelectionHandler): ServerModCommand {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandBuilder()
                .importDependencies(
                    DependencyFabric()
                        .register("selection", selection)
                        .createDeps()
                )
                .printErrors()
                .buildTree(Comma::class.java)
            .getCommand()
            .getLiteral()
        )
    }

    @Command("music")
    class Comma() {

        @Execute
        fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
            val player = context.source.player as ServerPlayerEntity
            val playlists = (deps.get("selection") as PlayerSelectionHandler).getSelectedPlaylists(player)
            if (playlists.isEmpty()) {
                player.sendMessage(Text.literal("Плейлисты не найдены."))
            } else {
                playlists.forEach { playlist ->
                    try {
                        player.sendMessage(playlist.getView().getText())
                    } catch (e: Exception) {
                        context.source.sendError(e.message?.formatMinecraft())
                    }
                }
            }
        }
    }

    companion object {
        fun formatTime(time: Int): String {
            val minutes = time / 60
            val seconds = time % 60
            return String.format("%02d:%02d", minutes, seconds)
        }
    }
}