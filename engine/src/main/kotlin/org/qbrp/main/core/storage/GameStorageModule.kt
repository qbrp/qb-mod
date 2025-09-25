package org.qbrp.main.core.storage

import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.main.core.database.Databases
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.modules.EngineModule

@Autoload(Priorities.HIGHEST)
class GameStorageModule: EngineModule("game-storage"), TableRegistry {
    companion object {
        private const val DATABASE_NAME = "gameStorage"
    }
    init {
        createModuleFileOnInit()
    }

    override fun getKoinModule(): Module = module {
        single {
            val archiveFile = getModuleFile().resolve("archive")
            Archiver(archiveFile)
        }
        single<TableRegistry> { this@GameStorageModule }
    }

    override fun getTable(name: String): Table {
        return Table(name,  DATABASE_NAME, Databases.MAIN_ASYNC)
    }
}