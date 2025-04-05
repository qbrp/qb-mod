package org.qbrp.core.game.player

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.core.game.registry.ServerModCommand
import org.qbrp.system.utils.format.Format.asMiniMessage

class NicknameCommand: ServerModCommand {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(CommandManager.literal("nick")
            .then(CommandManager.argument("nickname", StringArgumentType.greedyString())
            .executes { ctx ->
                val nick = StringArgumentType.getString(ctx, "nickname")
                PlayerManager.getPlayerSession(ctx.source.player!!.name.string)?.updateCustomName(nick)
                ctx.source.sendMessage("<gray>Никнейм установлен на $nick".asMiniMessage())
            1
        }))
        dispatcher.register(CommandManager.literal("resetnick")
            .executes { ctx ->
                PlayerManager.getPlayerSession(ctx.source.player!!.name.string)?.resetCustomName()
                ctx.source.sendMessage("<gray>Никнейм сброшен.".asMiniMessage())
                1
            })
    }
}