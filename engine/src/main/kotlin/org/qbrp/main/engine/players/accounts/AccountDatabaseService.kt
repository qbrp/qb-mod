package org.qbrp.main.engine.players.accounts

import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.io.UnsafeIoApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bson.Document
import org.qbrp.main.core.database.CoroutineDatabaseClient
import org.qbrp.main.core.components.serialization.Serialization
import org.qbrp.main.core.storage.Table
import org.qbrp.main.engine.players.model.ServerPlayerObject

class AccountDatabaseService(tableName: String,
                             databaseName: String,
                             client: CoroutineDatabaseClient
) : Table(tableName, databaseName, client) {
    val scope = CoroutineScope(Dispatchers.IO)
    val mongo = database

    suspend fun upsertAccount(playerObject: ServerPlayerObject) {
        try {
            playerObject.account = get(playerObject.account.accountId)!!
        } catch (e: Exception) {
            throw e
        }
    }

    fun saveAccountGameState(account: Account, updates: List<AccountUpdate>) = scope.launch(Dispatchers.IO) {
        val accountUuid = account.accountId.toString()
        val filter = Filters.eq("id", accountUuid)
        val collection = mongo.getCollection<Document>("data")

        val bsonUpdates = updates.flatMap { it.updates }
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
        saveObject(
            account,
            Json.encodeToString(account),
        )
    }


    suspend fun get(uuid: String): Account? = withContext(Dispatchers.IO) {
        try {
            val matches: Document? = getByField("id", uuid).await()
            if (matches == null) {
                return@withContext null
            }
            return@withContext Serialization.JSON.decodeFromString<Account>(matches.toJson())
        } catch (e: Exception) {
            LOGGER.error("Ошибка при получении аккаунта с UUID $uuid: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }


    suspend fun isNicknameAlreadyRegistered(nickname: String): Boolean = withContext(Dispatchers.IO) {
        val match = getByField("nicknames", nickname).await()
        return@withContext match != null
    }
}