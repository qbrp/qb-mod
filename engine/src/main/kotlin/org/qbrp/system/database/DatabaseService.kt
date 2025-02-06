package org.qbrp.system.database


import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.mongodb.client.MongoDatabase
import com.mongodb.client.MongoClient
import com.mongodb.MongoException
import org.bson.Document
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import org.litote.kmongo.KMongo
import org.qbrp.system.utils.log.Loggers
import kotlin.text.toList

class DatabaseService(private val url: String, private val dbName: String) {
    private var client: MongoClient? = null
    private var db: MongoDatabase? = null
    private val logger = Loggers.get("database")

    private val objectMapper: ObjectMapper = ObjectMapper().apply {
        registerKotlinModule()
    }

    fun connect(): Boolean {
        logger.log("Подключение к базе данных $dbName через $url")
        return try {
            client = KMongo.createClient(url)
            db = client?.getDatabase(dbName)

            // Проверяем, если клиент подключен, используя команду ping
            var attempts = 0
            while (attempts < 30) {
                try {
                    client?.getDatabase(dbName)
                        ?.runCommand(Document("ping", 1))  // Отправляем команду ping, чтобы проверить соединение
                    logger.log("Успешно подключено к базе данных $dbName.")
                    return true
                } catch (e: MongoException) {
                    attempts++
                    logger.log("Ожидание подключения к базе данных... Попытка #$attempts")
                    Thread.sleep(1000)  // Ожидаем 1 секунду перед новой попыткой
                }
            }

            logger.error("Не удалось подключиться к базе данных $dbName за 30 попыток.")
            false
        } catch (e: MongoException) {
            logger.error("Ошибка подключения к базе данных $dbName: ${e.message}")
            false
        }
    }


    fun disconnect() {
        client?.close()
        logger.log("Соединение с базой данных закрыто.")
    }

    fun updateField(document: String, query: Map<String, Any>, field: String, value: Any): Boolean = try {
        logger.log("Обновление поля $field в документе $document с фильтром $query значением $value")
        db?.getCollection(document)?.updateOne(
            Filters.and(query.map { Filters.eq(it.key, it.value) }),
            Document("\$set", Document(field, value))
        )?.modifiedCount?.let { it > 0 } ?: false
    } catch (e: MongoException) {
        logError("Обновление поля $field в документе $document", e)
        false
    }

    private fun <T> serializeObjectToMap(obj: T): Map<String, Any> {
        val json = objectMapper.writeValueAsString(obj)  // Сериализуем объект в строку JSON
        return objectMapper.readValue(json, Map::class.java) as Map<String, Any>  // Десериализуем JSON в Map
    }

    fun <T> insertObject(document: String, obj: T): String? = try {
        val data = serializeObjectToMap(obj)  // Сериализуем объект
        db?.getCollection(document)?.insertOne(Document(data))?.insertedId?.asObjectId()?.value?.toHexString()
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
        db?.getCollection(document)?.updateOne(filter, updateDoc, options)?.let { true } ?: false
    } catch (e: MongoException) {
        logError("Upsert объекта в коллекцию $document", e)
        false
    }

    fun upsert(document: String, query: Map<String, Any>, updates: Map<String, Any>): Boolean = try {
        logger.log("Upsert данных в коллекцию $document с фильтром $query и обновлениями $updates")
        val filter = Filters.and(query.map { Filters.eq(it.key, it.value) })
        val updateDoc = Document("\$set", Document(updates))
        val options = UpdateOptions().upsert(true)
        db?.getCollection(document)?.updateOne(filter, updateDoc, options)?.let { true } ?: false
    } catch (e: MongoException) {
        logError("Upsert данных в коллекцию $document", e)
        false
    }

    fun insert(document: String, data: Map<String, Any>): String? = try {
        logger.log("Вставка данных в коллекцию $document с данными $data")
        db?.getCollection(document)?.insertOne(Document(data))?.insertedId?.asObjectId()?.value?.toHexString()
    } catch (e: MongoException) {
        logError("Вставка данных в коллекцию $document", e)
        null
    }

    fun delete(document: String, query: Map<String, Any>): Boolean = try {
        logger.log("Удаление документа из коллекции $document с фильтром $query")
        db?.getCollection(document)?.deleteOne(
            Filters.and(query.map { Filters.eq(it.key, it.value) })
        )?.deletedCount?.let { it > 0 } ?: false
    } catch (e: MongoException) {
        logError("Удаление документа из коллекции $document", e)
        false
    }

    fun fetchAll(document: String, query: Map<String, Any>, clazz: Class<*>): List<*> {
        return try {
            logger.log("Получение всех документов из коллекции $document с фильтром $query")
            val collection = db?.getCollection(document)
            val documents = if (query.isEmpty()) {
                collection?.find()?.toList()  // Запрос без фильтров
            } else {
                collection?.find(
                    Filters.and(query.map { Filters.eq(it.key, it.value) })
                )?.toList()
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
        db?.getCollection(document)?.find(
            Filters.and(query.map { Filters.eq(it.key, it.value) })
        )?.firstOrNull()
    } catch (e: MongoException) {
        logError("Получение одного документа из коллекции $document", e)
        null
    }

    fun fetchAll(document: String, query: Map<String, Any>): List<Document> = try {
        logger.log("Получение всех документов из коллекции $document с фильтром $query")
        db?.getCollection(document)?.find(
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