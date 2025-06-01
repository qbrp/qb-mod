package org.qbrp.main.core.database

import com.mongodb.MongoException
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import org.bson.Document
import org.litote.kmongo.KMongo
import org.qbrp.main.core.utils.log.LoggerUtil

class SynchronousDatabaseClient(private val url: String): DatabaseClient<MongoDatabase> {
    private var connected: Boolean = false
    private var client: MongoClient? = null
    private val logger = LoggerUtil.get("database")

    fun connect(): Boolean {
        logger.log("Подключение к MongoDB по адресу $url")
        return try {
            client = KMongo.createClient(url)
            repeat(30) { attempt ->
                try {
                    client!!
                        .getDatabase("admin")
                        .runCommand(Document("ping", 1))
                    logger.log("Успешно подключено к MongoDB.")
                    connected = true
                    return true
                } catch (e: MongoException) {
                    logger.log("Ожидание подключения к MongoDB... Попытка #${attempt + 1}")
                    Thread.sleep(1_000)
                }
            }
            logger.error("Не удалось подключиться к MongoDB за 30 попыток.")
            false
        } catch (e: MongoException) {
            logger.error("Ошибка подключения к MongoDB: ${e.message}")
            false
        }
    }

    override fun getDatabase(dbName: String): MongoDatabase {
        if (!connected) connect()
        return client?.getDatabase(dbName) ?: throw MongoException("Could not open database $dbName")
    }

    fun disconnect() {
        client?.close()
        logger.log("Соединение с MongoDB закрыто.")
    }
}
