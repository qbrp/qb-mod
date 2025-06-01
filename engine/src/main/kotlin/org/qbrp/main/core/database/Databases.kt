package org.qbrp.main.core.database

import org.koin.core.context.GlobalContext
import org.qbrp.deprecated.resources.data.config.ServerConfigData

object Databases {
    val MAIN_ASYNC = CoroutineDatabaseClient(
        GlobalContext.get().get<ServerConfigData>().databases.nodeUri
    )
    val DEFAULT = SynchronousDatabaseClient(
        GlobalContext.get().get<ServerConfigData>().databases.nodeUri
    )
}