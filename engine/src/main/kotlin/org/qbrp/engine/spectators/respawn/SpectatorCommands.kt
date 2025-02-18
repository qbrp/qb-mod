package org.qbrp.engine.spectators.respawn

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.core.game.commands.CommandBuilder
import org.qbrp.core.game.commands.Deps
import org.qbrp.core.game.commands.annotations.Arg
import org.qbrp.core.game.commands.annotations.Command
import org.qbrp.core.game.commands.annotations.Execute
import org.qbrp.core.game.commands.templates.CallbackCommand
import org.qbrp.core.game.registry.ServerModCommand
import org.qbrp.engine.Engine

class SpectatorCommands(): ServerModCommand {

    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandBuilder()
                .buildTree(this::class.java)
                .getCommand()
                .getLiteral()
        )
    }

    @Command("qbs")
    class Spawn {
        @Execute
        fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
            Engine.spectatorsModule.API.removeRespawnSpectator(context.source.player!!)
        }
    }

    @Command("ignoreqbs")
    class IgnoreSpawnMessage {
        @Execute
        fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
            Engine.spectatorsModule.API.removeRespawnMessage(context.source.player!!)
        }
    }

    @Command("giveqbs")
    class GiveSpectator(@Arg val playerName: String): CallbackCommand() {
        @Execute
        fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
            Engine.spectatorsModule.API.setRespawnSpectator(context.source.world.players.find { it.name.string == playerName } ?: return callback(context, "&cИгрок не найден"))
            callback(context, "Режим спектатора назначен игроку $playerName")
        }
    }
}