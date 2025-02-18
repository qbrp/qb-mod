package org.qbrp.system.utils.log

import org.qbrp.system.utils.keys.LoggerKey

object Loggers {
    private val loggers = mutableMapOf<LoggerKey, Logger>()

    init {
        register(Logger(enabled = true,"mixin"))
        register(Logger(enabled = false,"database"))
        register(Logger(enabled = true,"plasmo"))
        register(Logger(enabled = true,"musicFade", "plasmoLoader"))
        register(Logger(enabled = true,"musicManager", "plasmoLoader"))
        register(Logger(enabled = true,"musicManager"))
        register(Logger(enabled = true,"musicManager", "playback"))
        register(Logger(enabled = true,"musicManager", "controller"))
        register(Logger(enabled = true,"audioManager"))
        register(Logger(enabled = true,"visualData", "storage"))
        register(Logger(enabled = true,"visualData", "loader"))
        register(Logger(enabled = true,"resources"))
        register(Logger(enabled = true,"timers"))
        register(Logger(enabled = true,"network", "sending"))
        register(Logger(enabled = false,"network", "receiving"))
        register(Logger(enabled = false,"serverNetwork", "sending"))
        register(Logger(enabled = false,"serverNetwork", "receiving"))
        register(Logger(enabled = false,"resources", "debug"))
        register(Logger(enabled = true,"items", "mechanics"))
        register(Logger(enabled = true,"regions"))
        register(Logger(enabled = true,"groups"))
        register(Logger(enabled = true,"spectatorsModule"))
        register(Logger(enabled = true,"chatModule"))
        register(Logger(enabled = false,"cluster"))
        register(Logger(enabled = true,"time"))
        register(Logger(enabled = true,"chat"))
        register(Logger(enabled = true,"chat", "broadcast"))
    }

    fun register(logger: Logger) { loggers[logger.key] = logger }
    fun get(vararg categories: String): Logger {
        val key = LoggerKey(categories.toList())
        return loggers[key] ?: throw IllegalArgumentException("'${categories.joinToString("/")}' не найден в реестре")
    }
}
