package org.qbrp.main.engine.players.nicknames

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.main.core.mc.player.PlayersModule
import org.qbrp.main.core.mc.commands.CommandRegistryEntry
import org.qbrp.main.core.mc.player.PlayersUtil
import org.qbrp.main.engine.players.nicknames.NicknamesModule.NicknameManager
import org.qbrp.main.core.utils.format.Format.asMiniMessage

class NicknameCommand: CommandRegistryEntry {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(CommandManager.literal("nick")
            .then(CommandManager.argument("nickname", StringArgumentType.greedyString())
            .executes { ctx ->
                getManager(ctx).update(getNick(ctx))
                ctx.source.sendMessage("<gray>Никнейм установлен на ${getNick(ctx)}".asMiniMessage())
            1
        }))
        dispatcher.register(CommandManager.literal("resetnick")
            .executes { ctx ->
                getManager(ctx).reset()
                ctx.source.sendMessage("<gray>Никнейм сброшен.".asMiniMessage())
                1
            })
    }

    private fun getManager(ctx: CommandContext<ServerCommandSource>): NicknameManager {
        return PlayersUtil.getPlayerSession(ctx.source.player!!).state.getComponentOrThrow<NicknameManager>()
    }

    private fun getNick(ctx: CommandContext<ServerCommandSource>): String {
        return StringArgumentType.getString(ctx, "nickname")
    }
}