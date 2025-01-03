package org.qbrp.system.database

import org.litote.kmongo.KMongo
import com.mongodb.client.MongoDatabase
import com.mongodb.client.MongoClient
import com.mongodb.MongoException
import org.bson.Document
import org.bson.conversions.Bson
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import org.qbrp.system.utils.log.Loggers

class DatabaseManager(private val url: String, private val dbName: String) {
    private var client: MongoClient? = null
    private var db: MongoDatabase? = null
    private val logger = Loggers.get("database")

    fun connect(): Boolean = try {
        client = KMongo.createClient(url)
        db = client?.getDatabase(dbName)
        true
    } catch (e: MongoException) {
        false
    }

    fun disconnect() = client?.close().also { println("Соединение закрыто.") }

    fun updateField(document: String, query: Map<String, Any>, field: String, value: Any): Boolean = try {
        db?.getCollection(document)?.updateOne(
            Filters.and(query.map { Filters.eq(it.key, it.value) }),
            Document("\$set", Document(field, value))
        )?.modifiedCount?.let { it > 0 } ?: false
    } catch (e: MongoException) {
        logError("Обновление поля", e)
        false
    }

    fun upsert(document: String, query: Map<String, Any>, updates: Map<String, Any>): Boolean = try {
        val filter = Filters.and(query.map { Filters.eq(it.key, it.value) })
        val updateDoc = Document("\$set", Document(updates))
        val options = UpdateOptions().upsert(true)
        db?.getCollection(document)?.updateOne(filter, updateDoc, options)?.let { true } ?: false
    } catch (e: MongoException) {
        logError("Upsert", e)
        false
    }

    fun insert(document: String, data: Map<String, Any>): String? = try {
        db?.getCollection(document)?.insertOne(Document(data))?.insertedId?.asObjectId()?.value?.toHexString()
    } catch (e: MongoException) {
        logError("Вставка", e)
        null
    }

    fun delete(document: String, query: Map<String, Any>): Boolean = try {
        db?.getCollection(document)?.deleteOne(
            Filters.and(query.map { Filters.eq(it.key, it.value) })
        )?.deletedCount?.let { it > 0 } ?: false
    } catch (e: MongoException) {
        logError("Удаление", e)
        false
    }

    fun fetchOne(document: String, query: Map<String, Any>): Document? = try {
        db?.getCollection(document)?.find(
            Filters.and(query.map { Filters.eq(it.key, it.value) })
        )?.firstOrNull()
    } catch (e: MongoException) {
        logError("Получение одного документа", e)
        null
    }

    fun fetchAll(document: String, query: Map<String, Any>): List<Document> = try {
        db?.getCollection(document)?.find(
            Filters.and(query.map { Filters.eq(it.key, it.value) })
        )?.toList() ?: emptyList()
    } catch (e: MongoException) {
        logError("Получение всех документов", e)
        emptyList()
    }

    private fun logError(action: String, e: MongoException) {
        logger.error("Ошибка $action: ${e.message}")
    }
}
