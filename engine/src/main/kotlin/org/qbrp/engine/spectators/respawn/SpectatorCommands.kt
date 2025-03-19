package org.qbrp.engine.spectators.respawn

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
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
                .buildTree(Spawn::class.java)
                .getCommand()
                .getLiteral()
        )
        dispatcher.register(
            CommandBuilder()
                .buildTree(IgnoreSpawnMessage::class.java)
                .getCommand()
                .getLiteral()
        )
        dispatcher.register(
            CommandBuilder()
                .buildTree(GiveSpectator::class.java)
                .getCommand()
                .getLiteral()
        )
    }

    @Command("qbs")
    class Spawn: KoinComponent {
        @Execute
        fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
            get<RespawnManager>().spawn(context.source.player!!)
        }
    }

    @Command("ignoreqbs")
    class IgnoreSpawnMessage: KoinComponent {
        @Execute
        fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
            get<RespawnManager>().ignore(context.source.player!!)
        }
    }

    @Command("giveqbs")
    class GiveSpectator(@Arg val playerName: String): CallbackCommand(), KoinComponent {
        @Execute
        fun execute(context: CommandContext<ServerCommandSource>, deps: Deps) {
            get<RespawnManager>().giveSpectator(context.source.world.players.find { it.name.string == playerName } ?: return callback(context, "&cИгрок не найден"))
            callback(context, "Режим спектатора назначен игроку $playerName")
        }
    }
}