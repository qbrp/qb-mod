package org.qbrp.core.game.player.registration

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.core.game.player.PlayerManager
import org.qbrp.core.game.registry.ServerModCommand
import org.qbrp.system.database.CoroutinesUtil
import org.qbrp.system.utils.format.Format.asMiniMessage

class AccountSyncCommand: ServerModCommand {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(CommandManager.literal("sync").executes() { ctx ->
            val session = PlayerManager.getPlayerSession(ctx.source.player!!)
            if (session.isAuthorized()) {
                CoroutinesUtil.runAsyncCommand(ctx, { session.database.upsertAccount() }, {
                    ctx.source.sendMessage("<green>Данные синхронизированы.".asMiniMessage())
                })
            }
            1
        })
    }
}