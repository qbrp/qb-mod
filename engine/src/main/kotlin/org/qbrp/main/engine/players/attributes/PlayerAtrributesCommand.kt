package org.qbrp.main.engine.players.attributes

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import org.koin.core.component.KoinComponent
import org.qbrp.main.core.components.extensions.require
import org.qbrp.main.core.fabricmc.commands.ServerCommandDispatcher
import org.qbrp.main.core.fabricmc.commands.registerCommand
import org.qbrp.main.core.fabricmc.player.hasPermission
import org.qbrp.main.core.util.format.Format.formatMm
import org.qbrp.main.engine.players.model.service.PlayersUtil.getPlayerSession

fun ServerCommandDispatcher.registerPlayerAttributesCommand() {
    register(
        literal("attr")
            .requires { it.player?.hasPermission("pmanager") == true }
            .then(argument("players", EntityArgumentType.players())
                .then(literal("speed")
                    .then(literal("set")
                            .then(argument("value", IntegerArgumentType.integer())
                                    .executes() { ctx ->
                                        getPlayers(ctx).forEach {
                                            getPlayerSession(it).require<PlayerAttributes>().speed = getValue(ctx)
                                        }
                                        ctx.source.sendMessage("<green>Скорость изменена".formatMm())
                                        1
                                    }))
                    .then(literal("reset")
                            .executes() { ctx ->
                                getPlayers(ctx).forEach {
                                    getPlayerSession(it).require<PlayerAttributes>().resetSpeed()
                                }
                                ctx.source.sendMessage("<green>Скорость сброшена".formatMm())
                                1
                            }
                    )
                )
            )
    )
}

private fun getPlayers(ctx: CommandContext<ServerCommandSource>): Collection<ServerPlayerEntity> {
    return EntityArgumentType.getPlayers(ctx, "players")
}

private fun getValue(ctx: CommandContext<ServerCommandSource>): Int {
    return IntegerArgumentType.getInteger(ctx, "value")
}