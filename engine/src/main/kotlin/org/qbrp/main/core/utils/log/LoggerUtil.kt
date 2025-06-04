package org.qbrp.main.core.utils.log

object LoggerUtil {
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
        register(Logger(enabled = false,"visualData", "storage"))
        register(Logger(enabled = false,"visualData", "loader"))
        register(Logger(enabled = true,"resources"))
        register(Logger(enabled = true,"timers"))
        register(Logger(enabled = false,"network", "sending"))
        register(Logger(enabled = false,"network", "receiving"))
        register(Logger(enabled = true,"downloading"))
        register(Logger(enabled = false,"serverNetwork", "sending"))
        register(Logger(enabled = false,"serverNetwork", "receiving"))
        register(Logger(enabled = false,"resources", "debug"))
        register(Logger(enabled = true,"regions"))
        register(Logger(enabled = true,"chatGroups"))
        register(Logger(enabled = true,"chatModule"))
        register(Logger(enabled = false,"cluster"))
        register(Logger(enabled = true,"time"))
        register(Logger(enabled = true,"chat"))
        register(Logger(enabled = false,"chat", "sending"))
        register(Logger(enabled = true,"chat", "receiving"))
        register(Logger(enabled = true,"chat", "broadcast"))
        register(Logger(enabled = true,"chat", "volume"))
        register(Logger(enabled = true,"info"))
        register(Logger(enabled = true,"engine"))
        register(Logger(enabled = true,"modules"))
        register(Logger(enabled = true,"build"))
        register(Logger(enabled = true,"game", "components"))
    }

    fun register(logger: Logger): Logger {
        loggers.set(logger.key, logger)
        return logger
    }

    fun get(vararg categories: String): Logger {
        val key = LoggerKey(categories.toList())
        return loggers[key] ?: register(Logger(true, key))
    }
}
