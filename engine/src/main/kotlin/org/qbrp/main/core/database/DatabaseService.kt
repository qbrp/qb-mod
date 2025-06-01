package org.qbrp.main.core.database


import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.mongodb.client.MongoDatabase
import com.mongodb.MongoException
import org.bson.Document
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import org.bson.conversions.Bson
import org.qbrp.main.core.utils.log.LoggerUtil

@Deprecated("Использовать gameStorage")
open class DatabaseService(protected val client: SynchronousDatabaseClient,
                           protected open val dbName: String,) {
    var mongo: MongoDatabase = client.getDatabase(dbName)
    protected val logger = LoggerUtil.get("database")

    private val objectMapper: ObjectMapper = ObjectMapper().apply {
        registerKotlinModule()
    }

    fun updateField(document: String, query: Map<String, Any>, field: String, value: Any): Boolean = try {
        logger.log("Обновление поля $field в документе $document с фильтром $query значением $value")
        mongo?.getCollection(document)?.updateOne(
            Filters.and(query.map { Filters.eq(it.key, it.value) }),
            Document("\$set", Document(field, value))
        )?.modifiedCount?.let { it > 0 } ?: false
    } catch (e: MongoException) {
        logError("Обновление поля $field в документе $document", e)
        false
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> serializeObjectToMap(obj: T): Map<String, Any> {
        val json = objectMapper.writeValueAsString(obj)  // Сериализуем объект в строку JSON
        return objectMapper.readValue(json, Map::class.java) as Map<String, Any>  // Десериализуем JSON в Map
    }

    fun <T> insertObject(document: String, obj: T): String? = try {
        val data = serializeObjectToMap(obj)  // Сериализуем объект
        mongo?.getCollection(document)?.insertOne(Document(data))?.insertedId?.asObjectId()?.value?.toHexString()
    } catch (e: MongoException) {
        logError("Вставка объекта в коллекцию $document", e)
        null
    }

    fun <T> upsertObject(document: String, query: Map<String, Any>, obj: T): Boolean = try {
        val updates = serializeObjectToMap(obj)  // Сериализуем объект
        logger.log("Upsert объекта в коллекцию $document с фильтром $query и данными $updates")
        val filter = Filters.and(query.map { Filters.eq(it.key, it.value) })
        val updateDoc = Document("\$set", Document(updates))
        val options = UpdateOptions().upsert(true)
        mongo?.getCollection(document)?.updateOne(filter, updateDoc, options)?.let { true } ?: false
    } catch (e: MongoException) {
        logError("Upsert объекта в коллекцию $document", e)
        false
    }

    fun upsert(document: String, query: Map<String, Any>, updates: Map<String, Any>): Boolean = try {
        logger.log("Upsert данных в коллекцию $document с фильтром $query и обновлениями $updates")
        val filter = Filters.and(query.map { Filters.eq(it.key, it.value) })
        val updateDoc = Document("\$set", Document(updates))
        val options = UpdateOptions().upsert(true)
        mongo.getCollection(document)?.updateOne(filter, updateDoc, options)?.let { true } ?: false
    } catch (e: MongoException) {
        logError("Upsert данных в коллекцию $document", e)
        false
    }

    fun insert(document: String, data: Map<String, Any>): String? = try {
        logger.log("Вставка данных в коллекцию $document с данными $data")
        mongo?.getCollection(document)?.insertOne(Document(data))?.insertedId?.asObjectId()?.value?.toHexString()
    } catch (e: MongoException) {
        logError("Вставка данных в коллекцию $document", e)
        null
    }

    fun delete(document: String, query: Map<String, Any>): Boolean = try {
        logger.log("Удаление документа из коллекции $document с фильтром $query")
        mongo?.getCollection(document)?.deleteOne(
            Filters.and(query.map { Filters.eq(it.key, it.value) })
        )?.deletedCount?.let { it > 0 } ?: false
    } catch (e: MongoException) {
        logError("Удаление документа из коллекции $document", e)
        false
    }

    fun fetchAll(
        document: String,
        query: Map<String, Any>,
        clazz: Class<*>,
        customFilters: List<Bson> = emptyList()
    ): List<*> {
        return try {
            logger.log("Получение всех документов из коллекции $document с фильтром $query и дополнительными фильтрами $customFilters")
            val collection = mongo?.getCollection(document)
            val queryFilters = query.map { Filters.eq(it.key, it.value) }
            val allFilters = queryFilters + customFilters
            val documents = if (allFilters.isEmpty()) {
                collection?.find()?.toList()  // Запрос без фильтров
            } else {
                collection?.find(Filters.and(allFilters))?.toList()
            }
            documents?.map { document ->
                objectMapper.readValue(document.toJson(), clazz)
            } ?: emptyList()
        } catch (e: MongoException) {
            logError("Получение всех документов из коллекции $document", e)
            emptyList<Any>()
        }
    }

    fun fetchOne(document: String, query: Map<String, Any>): Document? = try {
        logger.log("Получение одного документа из коллекции $document с фильтром $query")
        mongo?.getCollection(document)?.find(
            Filters.and(query.map { Filters.eq(it.key, it.value) })
        )?.firstOrNull()
    } catch (e: MongoException) {
        logError("Получение одного документа из коллекции $document", e)
        null
    }

    fun fetchAll(document: String, query: Map<String, Any>): List<Document> = try {
        logger.log("Получение всех документов из коллекции $document с фильтром $query")
        mongo?.getCollection(document)?.find(
            Filters.and(query.map { Filters.eq(it.key, it.value) })
        )?.toList() ?: emptyList()
    } catch (e: MongoException) {
        logError("Получение всех документов из коллекции $document", e)
        emptyList()
    }

    private fun logError(action: String, e: MongoException) {
        logger.error("Ошибка $action: ${e.message}")
    }
}