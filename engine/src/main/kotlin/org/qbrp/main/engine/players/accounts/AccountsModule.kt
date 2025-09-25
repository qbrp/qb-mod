package org.qbrp.main.engine.players.accounts

import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.main.core.database.Databases
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.modules.EngineModule

@Autoload(Priorities.PLAYERS + 2)
class AccountsModule: EngineModule("accounts") {
    override fun getKoinModule(): Module = module {
        single { AccountDatabaseService("data", "players", Databases.MAIN_ASYNC) }
    }
}