package org.qbrp.main.engine.spectators

import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager
import org.qbrp.main.core.fabricmc.commands.ServerCommandDispatcher
import org.qbrp.main.core.fabricmc.unlockActionBarStatus
import org.qbrp.main.core.util.format.Format.formatMm
import org.qbrp.main.engine.players.model.engine

fun ServerCommandDispatcher.registerSpectatorCommands() {
    register(
        CommandManager.literal("spawn").executes { ctx ->
            ctx.source.player?.engine()?.stopSpectating()
            1
        }
    )
    register(
        CommandManager.literal("ignoreqbs").executes { ctx ->
            val player = ctx.source.player?.engine()
            if (player?.isSpectating == true) {
                player.unlockActionBarStatus()
            }
            1
        }
    )
    register(
        CommandManager.literal("giveqbs")
            .then(
                CommandManager.argument("player", EntityArgumentType.players())
                    .executes { ctx ->
                        val players = EntityArgumentType.getPlayers(ctx, "player")
                        players.forEach { player ->
                            player.engine().startSpectating()
                        }
                        ctx.source.sendMessage("Режим спавна назначен игрокам ${players.joinToString(", ") { it.name.string }}".formatMm())
                        1
                    }
            )
    )
}