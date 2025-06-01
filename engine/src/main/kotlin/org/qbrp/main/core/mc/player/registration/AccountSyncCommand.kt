package org.qbrp.main.core.mc.player.registration

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.main.core.mc.commands.CommandRegistryEntry
import org.qbrp.main.core.mc.player.PlayersUtil
import org.qbrp.main.core.mc.player.service.AccountDatabaseService
import org.qbrp.main.core.utils.CoroutinesUtil
import org.qbrp.main.core.utils.format.Format.asMiniMessage

class AccountSyncCommand(val accounts: AccountDatabaseService): CommandRegistryEntry {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(CommandManager.literal("sync").executes() { ctx ->
            val session = PlayersUtil.getPlayerSession(ctx.source.player!!)
            CoroutinesUtil.runAsyncCommand(
                ctx,
                { accounts.upsertAccount(session) },
                { ctx.source.sendMessage("<green>Данные синхронизированы.".asMiniMessage()) },
                { ctx.source.sendMessage("<red>Возникла ошибка при обновлении аккаунта: ${it.message}.".asMiniMessage()) })
            1
        })
    }
}