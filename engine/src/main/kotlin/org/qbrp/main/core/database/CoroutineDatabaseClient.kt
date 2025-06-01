package org.qbrp.main.core.database

import com.mongodb.MongoException
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.bson.Document
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.reactivestreams.*
import org.litote.kmongo.coroutine.coroutine
import org.qbrp.main.core.utils.log.LoggerUtil

class CoroutineDatabaseClient(private val url: String): DatabaseClient<CoroutineDatabase> {
    private var connected: Boolean = false
    private var client: CoroutineClient? = null
    private val logger = LoggerUtil.get("database")

    suspend fun connect(): Boolean {
        logger.log("Подключение к MongoDB по адресу $url")
        return try {
            client = KMongo.createClient(url).coroutine
            repeat(30) { attempt ->
                try {
                    client!!
                        .getDatabase("admin")
                        .runCommand<Document>(Document("ping", 1))
                    logger.log("Успешно подключено к MongoDB.")
                    connected = true
                    return true
                } catch (e: MongoException) {
                    logger.log("Ожидание подключения к MongoDB... Попытка #${attempt + 1}")
                    delay(1_000)
                }
            }
            logger.error("Не удалось подключиться к MongoDB за 30 попыток.")
            false
        } catch (e: MongoException) {
            logger.error("Ошибка подключения к MongoDB: ${e.message}")
            false
        }
    }

    override fun getDatabase(dbName: String): CoroutineDatabase {
        if (!connected) runBlocking { connect() }
        return client?.getDatabase(dbName) ?: throw MongoException("Could not open database $dbName")
    }

    fun disconnect() {
        client?.close()
        logger.log("Соединение с MongoDB закрыто.")
    }
}
