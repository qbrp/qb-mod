package org.qbrp.engine.chat.addons.tools

import org.qbrp.system.utils.format.Format.stripFormatting

object MessageFormatTools {
    fun optimizeFormatting(input: String): String {
        if (input.isEmpty()) return input
        val resetRegex = Regex("&[rf]+")
        val parts = resetRegex.split(input)
        val optimizedParts = parts.filter { it.isNotBlank() }
        return optimizedParts.joinToString("&r")
    }

    fun addFormatting(input: String, formatString: String): String {
        if (input.isEmpty()) return input
        val optimizedInput = optimizeFormatting(input)
        val parts = optimizedInput.split(Regex("&[rf]"))

        val formattedParts = parts.mapIndexed { index, part ->
            if (index < parts.size - 1) {
                "$formatString$part&r"
            } else {
                "$formatString$part"
            }
        }

        // Объединяем части обратно
        return formattedParts.joinToString("")
    }

    fun setFormatting(input: String, formatString: String): String {
        return "$formatString${input.stripFormatting()}"
    }
}