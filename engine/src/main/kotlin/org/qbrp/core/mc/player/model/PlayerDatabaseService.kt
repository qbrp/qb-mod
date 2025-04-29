package org.qbrp.core.mc.player.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.litote.kmongo.insertOne
import org.qbrp.core.game.database.ObjectDatabaseService
import org.qbrp.core.resources.ServerResources

class PlayerDatabaseService : ObjectDatabaseService(ServerResources.getConfig().databases.nodeUri, "players") {
}