package org.qbrp.main.core.utils.log

import org.qbrp.main.core.utils.format.ConsoleColors
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Logger(val enabled: Boolean = true, val key: LoggerKey) {
    constructor(enabled: Boolean, vararg loggerKey: String): this(enabled, LoggerKey(loggerKey.toList()))
    private val logger: Logger = LoggerFactory.getLogger("qbrp/$key")

    fun warn(message: String) { log(message, LogType.WARN) }
    fun error(message: String) { log(message, LogType.ERROR) }
    fun success(message: String) { log(message, LogType.SUCCESS) }

    fun log(message: String, type: LogType = LogType.INFO) {
        val formattedMessage = formatMessage(message)
        if (enabled) {
            when (type) {
                LogType.INFO -> logger.info(formattedMessage)
                LogType.WARN -> logger.info("${ConsoleColors.ORANGE}$formattedMessage")
                LogType.SUCCESS -> logger.info("${ConsoleColors.GREEN}$formattedMessage")
                LogType.ERROR -> logger.error("${ConsoleColors.RED}$formattedMessage")
            }
        } else if(type == LogType.ERROR) {
            logger.error("${ConsoleColors.RED}$formattedMessage")
        }
    }

    private fun formatMessage(message: String): String {
        val regex = Regex("""<<(.*?)>>""") // Регулярное выражение для поиска текста в << >>
        return regex.replace(message) { matchResult ->
            "${ConsoleColors.BLUE}${matchResult.groupValues[1]}${ConsoleColors.RESET}"
        }
    }
}
