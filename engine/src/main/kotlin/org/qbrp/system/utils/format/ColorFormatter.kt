package org.qbrp.system.utils.format

import java.util.regex.Pattern

object ColorFormatter {
    private val colorPattern = Pattern.compile("\\{([a-zA-Z_,#0-9]+)\\}")

    fun String.formatColors(): String {
        val matcher = colorPattern.matcher(this)
        val result = StringBuffer()

        while (matcher.find()) {
            val tag = matcher.group(1) ?: ""
            val replacement = when {
                tag.startsWith("#") -> convertHexToAnsi(tag)
                tag.contains(",") -> tag.split(",").joinToString("") { ConsoleColors.aliases[it.trim()] ?: "" }
                else -> ConsoleColors.aliases[tag] ?: ""
            }
            matcher.appendReplacement(result, replacement)
        }

        matcher.appendTail(result)
        return result.toString() + ConsoleColors.RESET
    }

    private fun convertHexToAnsi(hex: String): String {
        return try {
            val color = Integer.parseInt(hex.substring(1), 16)
            "\u001B[38;2;${color shr 16};${(color shr 8) and 0xFF};${color and 0xFF}m"
        } catch (e: Exception) {
            ""
        }
    }
}
