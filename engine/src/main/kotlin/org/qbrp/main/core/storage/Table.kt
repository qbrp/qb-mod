package org.qbrp.main.core.storage

import com.mongodb.client.model.Filters
import com.mongodb.client.model.ReplaceOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.bson.Document
import org.koin.core.context.GlobalContext
import org.qbrp.main.core.database.CoroutineDatabaseClient
import org.qbrp.main.core.components.serialization.Identifiable
import org.qbrp.main.core.util.log.LoggerUtil

open class Table(val tableName: String,
                 databaseName: String,
                 database: CoroutineDatabaseClient,
                 protected val archiver: Archiver = GlobalContext.get().get()
): TableAccess, ArchiveAccess {
    protected val database = database.getDatabase(databaseName)
    protected val coroutineScope = CoroutineScope(Dispatchers.IO)
    companion object {
        val LOGGER = LoggerUtil.get("database")
        const val ID_FIELD_NAME = "id"
    }

    override fun saveObject(id: String, json: String) {
        coroutineScope.launch {
            try {
                val document = Document.parse(json)
                val filter = Filters.eq(ID_FIELD_NAME, id)
                database
                    .getCollection<Document>(tableName)
                    .replaceOne(
                        filter,
                        document,
                        ReplaceOptions().upsert(true)
                    )
            } catch (e: Exception) {
                LOGGER.error("Ошибка при сохранении объекта в '$tableName': ${e.message}")
                e.printStackTrace()
            }
        }
    }

    override fun getByField(name: String, value: Any): Deferred<Document?> {
        return coroutineScope.async {
            try {
                val filter = Filters.eq(name, value)
                database
                    .getCollection<Document>(tableName)
                    .find(filter)
                    .first()
            } catch (e: Exception) {
                LOGGER.error("Ошибка при чтении из '$tableName' по полю '$name': ${e.message}")
                e.printStackTrace()
                null
            }
        }
    }

    override fun getById(id: Any): Deferred<Document?> {
        return coroutineScope.async {
            val filter = Filters.eq(ID_FIELD_NAME, id)
            database.getCollection<Document>(tableName).find(filter).first()
        }
    }

    override fun getAll(): Deferred<List<Document>> {
        return coroutineScope.async {
            database
                .getCollection<Document>(tableName)
                .find()
                .toList()
        }
    }

    override fun archive(obj: Identifiable) {
        coroutineScope.launch {
            try {
                val filter = Filters.eq(ID_FIELD_NAME, obj.id)
                val collection = database.getCollection<Document>(tableName)
                val existingDoc = collection.find(filter).first()
                if (existingDoc != null) {
                    val json = existingDoc.toJson()
                    archiver.archive(json)
                    collection.deleteMany(filter)
                } else {
                    LOGGER.warn("При архивации объекта с $ID_FIELD_NAME='${obj.id}' документ не найден в '$tableName'")
                }
            } catch (e: Exception) {
                LOGGER.error("Ошибка при архивации объекта ($ID_FIELD_NAME='${obj.id}') из '$tableName': ${e.message}")
                e.printStackTrace()
            }
        }
    }
}