package org.qbrp.main.core.storage

import org.qbrp.main.core.database.CoroutineDatabaseClient
import org.qbrp.main.core.game.serialization.Identifiable
import org.qbrp.main.core.utils.log.LoggerUtil
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Archiver(private val archiveFile: File) {
    companion object {
        private val LOGGER = LoggerUtil.get("archiver")
    }

    fun archive(json: String) {
        try {
            val date = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
            val target = archiveFile.resolve("archive_$date.json")
            target.writeText(json)
        } catch (e: Exception) {
            LOGGER.error("Не удалось записать в файл архив: ${e.message}")
            e.printStackTrace()
        }
    }
}