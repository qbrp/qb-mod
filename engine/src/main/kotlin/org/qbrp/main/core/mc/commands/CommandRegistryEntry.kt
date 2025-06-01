package org.qbrp.main.core.mc.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.utils.format.Format.asMiniMessage

interface CommandRegistryEntry {
    fun register(dispatcher: CommandDispatcher<ServerCommandSource>)

    companion object {
        fun requirePlayer(ctx: CommandContext<ServerCommandSource>): ServerPlayerEntity? {
            return ctx.source.player ?:
                run {
                    ctx.source.sendMessage("Вы должны быть игроком, чтобы активировать команду".asMiniMessage());
                    return null
                }
        }
    }
}