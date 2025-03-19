package org.qbrp.engine.music.plasmo.view

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import org.qbrp.core.ServerCore
import org.qbrp.core.game.registry.ServerModCommand
import org.qbrp.core.game.commands.CommandBuilder
import org.qbrp.core.game.commands.DependencyFabric
import org.qbrp.core.game.commands.Deps
import org.qbrp.core.game.commands.annotations.Arg
import org.qbrp.core.game.commands.annotations.Command
import org.qbrp.core.game.commands.annotations.Execute
import org.qbrp.core.game.commands.annotations.Provider
import org.qbrp.core.game.commands.templates.CallbackCommand
import org.qbrp.core.game.commands.templates.ListProvider
import org.qbrp.engine.music.plasmo.playback.PlayerSelectionHandler
import org.qbrp.system.utils.format.Format.formatMinecraft

class MusicViewCommand(val selection: PlayerSelectionHandler): ServerModCommand {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val builder = CommandBuilder()
            .importDependencies(
                DependencyFabric()
                    .register("selection", selection)
                    .createDeps()
            )
            .printErrors()
        dispatcher.register(
            builder.buildTree(Music::class.java).getCommand().getLiteral()
        )
        dispatcher.register(
            builder.buildTree(MusicFor::class.java).getCommand().getLiteral()
        )
        dispatcher.register(
            builder.buildTree(PlayerMusic::class.java).getCommand().getLiteral()
        )
    }

    @Command("listmusic")
    class Music() {

        @Execute(operatorLevel = 4)
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

    @Command("plrmusic")
    class PlayerMusic(@Arg @Provider(PlayerProvider::class) val name: String): CallbackCommand() {
        @Execute(operatorLevel = 4)
        fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
            val player = (deps.get("selection") as PlayerSelectionHandler).musicPlayerManager.playerManager.getPlayer(name) ?: return callback(context, "Игрок не найден")
            val session = (deps.get("selection") as PlayerSelectionHandler).getPlayerSession(player) ?: return callback(context, "Плейлисты не найдены")
            val view = session.getView().also { it.session == session }
            callback(context, view.getText())
        }
    }

    @Command("mymusic")
    class MusicFor: CallbackCommand() {
        @Execute
        fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
            val player = context.source.player as ServerPlayerEntity
            val session = (deps.get("selection") as PlayerSelectionHandler).getPlayerSession(player) ?: return callback(context, "Плейлисты не найдены")
            val view = session.getView().also { it.session == session }
            callback(context, view.getText())
        }
    }

    companion object {
        fun formatTime(time: Int): String {
            val minutes = time / 60
            val seconds = time % 60
            return String.format("%02d:%02d", minutes, seconds)
        }
    }

    class PlayerProvider() : ListProvider<String>( { ServerCore.server.playerManager.playerList.map { it.name.string } } )
}