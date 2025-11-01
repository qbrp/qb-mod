package org.qbrp.main.engine.players.model

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.main.core.fabricmc.commands.CommandRegistryEntry
import org.qbrp.main.core.util.format.Format.formatMm
import org.qbrp.main.core.util.runAsyncCommand
import org.qbrp.main.engine.players.accounts.AccountDatabaseService
import org.qbrp.main.engine.players.model.service.PlayersUtil

class AccountSyncCommand(val accounts: AccountDatabaseService): CommandRegistryEntry {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(CommandManager.literal("sync").executes() { ctx ->
            val session = PlayersUtil.getPlayerSession(ctx.source.player!!)
            runAsyncCommand(
                ctx,
                { accounts.upsertAccount(session) },
                { ctx.source.sendMessage("<green>Данные синхронизированы.".formatMm()) },
                { ctx.source.sendMessage("<red>Возникла ошибка при обновлении аккаунта: ${it.message}.".formatMm()) })
            1
        })
    }
}