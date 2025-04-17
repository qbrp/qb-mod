package org.qbrp.engine.spectators.respawn

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.EntityArgumentType
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
import org.qbrp.system.utils.format.Format.asMiniMessage

class SpectatorCommands(): ServerModCommand, KoinComponent {
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
        dispatcher.register(CommandManager.literal("giveqbs")
            .then(CommandManager.argument("player", EntityArgumentType.players())
                .executes { ctx ->
                    val players = EntityArgumentType.getPlayers(ctx, "player")
                    players.forEach { player ->
                        get<RespawnManager>().giveSpectator(player)
                    }
                    ctx.source.sendMessage("Режим спавна назначен игрокам ${players.map { it.name.string }.joinToString(", ")}".asMiniMessage())
                    1
                }
            )
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
}