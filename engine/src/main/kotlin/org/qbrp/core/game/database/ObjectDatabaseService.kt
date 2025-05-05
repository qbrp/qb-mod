package org.qbrp.core.game.database

import com.mongodb.client.model.Filters
import com.mongodb.client.model.ReplaceOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bson.Document
import org.qbrp.core.game.model.objects.BaseObject
import org.qbrp.core.game.serialization.ObjectJsonField
import org.qbrp.core.game.serialization.SerializeFabric
import org.qbrp.system.database.DatabaseService

open class ObjectDatabaseService(url: String, val tableName: String) : DatabaseService(url, "gameStorage") {
    open suspend fun <T: ObjectJsonField> saveObject(obj: T, table: String = tableName) {
        val json = SerializeFabric.MAPPER.writeValueAsString(obj)
        val document = Document.parse(json)
        val filter = Filters.eq("id", obj.id)
        db!!.getCollection(table)
            .replaceOne(
                filter,
                document,
                ReplaceOptions().upsert(true)
            )
    }

    open suspend fun destroyByField(name: String, value: Any) = withContext(Dispatchers.IO) {
        val filter = Filters.eq(name, value)
        return@withContext db!!.getCollection(tableName).deleteOne(filter)
    }

    open suspend fun <T : ObjectJsonField> archive(obj: T) = withContext(Dispatchers.IO) {
        saveObject(obj, "archive")
        val filter = Filters.eq("id", obj.id)
        return@withContext db!!.getCollection(tableName).deleteOne(filter)
    }

    open suspend fun <T : ObjectJsonField> getByField(name: String, value: Any, clazz: Class<T>): T? = withContext(Dispatchers.IO) {
        val filter = Filters.eq(name, value)
        val document = db!!.getCollection(tableName).find(filter).firstOrNull()

        return@withContext document?.let {
            SerializeFabric.MAPPER.readValue(it.toJson(), clazz)
        }
    }

    open suspend fun <T : ObjectJsonField> loadObjectId(id: Long, clazz: Class<T>): T? = withContext(Dispatchers.IO) {
        getByField("id", id, clazz)
    }

}