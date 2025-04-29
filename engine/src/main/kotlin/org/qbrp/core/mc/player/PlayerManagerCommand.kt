package org.qbrp.core.mc.player

import PermissionManager.hasPermission
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.core.mc.player.PlayerManager.getPlayerSession
import org.qbrp.core.mc.registry.ServerModCommand
import org.qbrp.system.utils.format.Format.asMiniMessage

class PlayerManagerCommand: ServerModCommand {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(CommandManager.literal("pmanager")
            .requires { it.player?.hasPermission("pmanager") == true }
            .then(argument("players", EntityArgumentType.players())
                .then(literal("speed")
                        .then(literal("set")
                            .then(argument("value", IntegerArgumentType.integer())
                                .executes() { ctx ->
                                    getPlayers(ctx).forEach {
                                        getPlayerSession(it).setSpeed(getValue(ctx))
                                    }
                                    ctx.source.sendMessage("<green>Скорость изменена".asMiniMessage())
                                    1
                                }))
                        .then(literal("reset")
                            .executes() { ctx ->
                                getPlayers(ctx).forEach {
                                    getPlayerSession(it).resetSpeed()
                                }
                                ctx.source.sendMessage("<green>Скорость сброшена".asMiniMessage())
                                1
                            }
                        ))))
    }

    fun getPlayers(ctx: CommandContext<ServerCommandSource>): Collection<ServerPlayerEntity> {
        return EntityArgumentType.getPlayers(ctx, "players")
    }

    fun getValue(ctx: CommandContext<ServerCommandSource>): Int {
        return IntegerArgumentType.getInteger(ctx, "value")
    }
}