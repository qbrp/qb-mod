package org.imperial_hell.ihSystems

import org.qbrp.system.utils.format.ConsoleColors
import org.qbrp.system.utils.keys.LoggerKey
import org.qbrp.system.utils.log.LogType
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Logger(val enabled: Boolean = true, vararg key: String) {
    val key = LoggerKey(key.toList())
    private val logger: Logger = LoggerFactory.getLogger("qbrp / ${key.toString()}}")

    fun warn(message: String) { log(message, LogType.WARN) }
    fun error(message: String) { log(message, LogType.ERROR) }
    fun success(message: String) { log(message, LogType.SUCCESS) }

    fun log(message: String, type: LogType = LogType.INFO) {
        if (enabled) {
            val formattedMessage = formatMessage(message)
            when (type) {
                LogType.INFO -> logger.info(formattedMessage)
                LogType.WARN -> logger.info("${ConsoleColors.ORANGE}$formattedMessage")
                LogType.ERROR -> logger.error(formattedMessage)
                LogType.SUCCESS -> logger.info("${ConsoleColors.GREEN}$formattedMessage") // Для успеха используем INFO
            }
        }
    }

    private fun formatMessage(message: String): String {
        val regex = Regex("""<<(.*?)>>""") // Регулярное выражение для поиска текста в << >>
        return regex.replace(message) { matchResult ->
            "${ConsoleColors.BLUE}${matchResult.groupValues[1]}${ConsoleColors.RESET}"
        }
    }
}
