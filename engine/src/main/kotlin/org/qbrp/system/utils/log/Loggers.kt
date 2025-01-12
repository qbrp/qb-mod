package org.qbrp.system.utils.log

import org.qbrp.system.utils.keys.LoggerKey

object Loggers {
    private val loggers = mutableMapOf<LoggerKey, Logger>()

    init {
        register(Logger(enabled = false,"database"))
        register(Logger(enabled = true,"plasmo"))
        register(Logger(enabled = true,"plasmo", "playback"))
        register(Logger(enabled = true,"plasmo", "controller"))
        register(Logger(enabled = true,"plasmo", "audioManager"))
        register(Logger(enabled = false,"visualData", "storage"))
        register(Logger(enabled = false,"visualData", "loader"))
        register(Logger(enabled = true,"resources"))
        register(Logger(enabled = true,"timers"))
        register(Logger(enabled = false,"network", "sending"))
        register(Logger(enabled = false,"network", "receiving"))
        register(Logger(enabled = false,"resources", "debug"))
        register(Logger(enabled = true,"items", "mechanics"))
    }

    fun register(logger: Logger) { loggers[logger.key] = logger }
    fun get(vararg categories: String): Logger {
        val key = LoggerKey(categories.toList())  // Создаем ключ с переданными категориями
        return loggers[key] ?: throw IllegalArgumentException("'${categories.joinToString("/")}' не найден в реестре")
    }
}
