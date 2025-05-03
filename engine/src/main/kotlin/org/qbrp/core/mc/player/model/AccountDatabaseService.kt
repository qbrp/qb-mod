package org.qbrp.core.mc.player.model

import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.io.UnsafeIoApi
import org.bson.conversions.Bson
import org.qbrp.core.game.database.ObjectDatabaseService
import org.qbrp.core.game.serialization.ObjectJsonField
import org.qbrp.core.mc.player.Account
import org.qbrp.core.mc.player.PlayerObject
import org.qbrp.core.mc.player.registration.LoginResult
import org.qbrp.core.resources.ServerResources

class AccountDatabaseService : ObjectDatabaseService(ServerResources.getConfig().databases.nodeUri, "data") {
    override val dbName = "players"
    val scope = CoroutineScope(Dispatchers.IO)

    fun saveAccountGameState(account: Account, updates: List<AccountUpdate>) = scope.launch(Dispatchers.IO) {
        val accountUuid = account.uuid.toString()
        val filter      = Filters.eq("uuid", accountUuid)
        val collection  = db!!.getCollection("data")

        val bsonUpdates     = updates.flatMap { it.updates }
        val allArrayFilters = updates.flatMap { it.arrayFilters }.distinct()

        val combinedUpdate = Updates.combine(bsonUpdates)

        if (allArrayFilters.isNotEmpty()) {
            val opts = UpdateOptions().arrayFilters(allArrayFilters)
            collection.updateOne(filter, combinedUpdate, opts)
        } else {
            collection.updateOne(filter, combinedUpdate)
        }
    }


    @UnsafeIoApi
    suspend fun save(account: Account) = withContext(Dispatchers.IO) {
        upsertObject<Account>(
            "data",
            mapOf("uuid" to account.uuid.toString()),
            account
        )
    }

    suspend fun get(uuid: String): Account? = withContext(Dispatchers.IO) {
        val matches = fetchAll("data", mapOf("uuid" to uuid), Account::class.java)
        matches.firstOrNull() as Account?
    }

    suspend fun isNicknameAlreadyRegistered(nickname: String): Boolean =
        withContext(Dispatchers.IO) {
            val matches = fetchAll(
                document = "data",
                query = emptyMap(),
                clazz = Account::class.java,
                customFilters = listOf(Filters.eq("nicknames", nickname))
            )
            matches.isNotEmpty()
        }

    suspend fun login(playerName: String, uuid: String): LoginResult = withContext(Dispatchers.IO) {
        get(uuid)?.let {
            if (!isNicknameAlreadyRegistered(playerName)) {
                it.updateRegisteredNicknames(playerName)
                LoginResult.SUCCESS
            } else {
                LoginResult.ALREADY_LINKED
            }
        } ?: LoginResult.NOT_FOUND
    }
}