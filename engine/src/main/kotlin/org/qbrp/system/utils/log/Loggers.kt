package org.qbrp.system.utils.log

import org.imperial_hell.ihSystems.Logger
import org.qbrp.system.utils.keys.LoggerKey

object Loggers {
    private val loggers = mutableMapOf<LoggerKey, Logger>()

    init {
        register(Logger(enabled = true,"resources"))
        register(Logger(enabled = false,"resources", "debug"))
        register(Logger(enabled = false,"items", "mechanics"))
    }

    fun register(logger: Logger) { loggers[logger.key] = logger }
    fun get(vararg categories: String): Logger {
        val key = LoggerKey(categories.toList())  // Создаем ключ с переданными категориями
        return loggers[key] ?: throw IllegalArgumentException("'${categories.joinToString(" / ")}' не найден в реестре")
    }
}