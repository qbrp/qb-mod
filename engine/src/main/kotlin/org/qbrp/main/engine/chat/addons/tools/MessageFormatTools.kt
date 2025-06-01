package org.qbrp.main.engine.chat.addons.tools

import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.core.utils.format.Format.stripFormatting

object MessageFormatTools {
    // Mapping of Minecraft color codes and named tags to HEX values
    private val colorMap = mapOf(
        // ampersand codes
        "&0" to "#000000", "&1" to "#0000AA", "&2" to "#00AA00", "&3" to "#00AAAA",
        "&4" to "#AA0000", "&5" to "#AA00AA", "&6" to "#FFAA00", "&7" to "#AAAAAA",
        "&8" to "#555555", "&9" to "#5555FF", "&a" to "#55FF55", "&b" to "#55FFFF",
        "&c" to "#FF5555", "&d" to "#FF55FF", "&e" to "#FFFF55", "&f" to "#FFFFFF",
        "&r" to "reset",
        // named tags
        "<black>" to "#000000", "<dark_blue>" to "#0000AA", "<dark_green>" to "#00AA00",
        "<dark_aqua>" to "#00AAAA", "<dark_red>" to "#AA0000", "<dark_purple>" to "#AA00AA",
        "<gold>" to "#FFAA00", "<gray>" to "#AAAAAA", "<dark_gray>" to "#555555",
        "<blue>" to "#5555FF", "<green>" to "#55FF55", "<aqua>" to "#55FFFF",
        "<red>" to "#FF5555", "<light_purple>" to "#FF55FF", "<yellow>" to "#FFFF55",
        "<white>" to "#FFFFFF"
    )

    /**
     * Replaces all ampersand color codes and named tags with HEX-format tags
     */
    fun convertToHexFormats(input: String): String {
        var result = input
        colorMap.forEach { (code, hex) ->
            if (true) {
                // replace code or tag with hex tag
                result = result.replace(code, "<${hex}>")
            } else {
                // remove reset codes &r
                result = result.replace(code, "<#FFFFFF>")
            }
        }
        return result
    }

    fun optimizeFormatting(input: String): String {
        if (input.isEmpty()) return input
        val resetRegex = Regex("(?:<#FFFFFF>|<reset>)+")
        val parts = resetRegex.split(input)
        val optimizedParts = parts.filter { it.isNotBlank() }
        return optimizedParts.joinToString("<reset>")
    }

    fun addFormatting(message: ChatMessage, formatString: String): String {
        val raw = MessageTextTools.getTextContent(message)
        if (raw.isEmpty()) return raw

        // 1) конвертируем все &-коды и теги в "<#RRGGBB>"
        val input = convertToHexFormats(raw)

        // 2) убираем повторяющиеся теги сброса
        val optimizedInput = input.split(Regex("&r|<#FFFFFF>|<reset>"))

        // 3) собираем обратно с нужным форматированием
        val formattedParts = optimizedInput.mapIndexed { idx, part ->
            if (part.isEmpty()) return@mapIndexed ""
            if (idx < optimizedInput.size - 1) "$formatString$part<#FFFFFF>"
            else "$formatString$part"
        }

        message.getTagsBuilder().component("formatString", convertToHexFormats(formatString))
        return formattedParts.joinToString("")
    }

    fun getFormatting(message: ChatMessage): String? {
        return message.getTags().getComponentData("formatString")
    }

    fun getFormattingHex(message: ChatMessage): String? {
        return message.getTags().getComponentData<String>("formatString")?.replace("<", "")?.replace(">", "")
    }

    fun setFormatting(message: ChatMessage, formatString: String): String {
        return "$formatString${MessageTextTools.getTextContent(message).stripFormatting()}"
    }

    fun stripFormatting(input: String): String {
        return convertToHexFormats(input).replace(
            Regex("<#[0-9A-Fa-f]{6}>|<reset>|&[0-9a-fr]", RegexOption.IGNORE_CASE),
            ""
        )
    }

    fun stripLeadingColor(input: String): String {
        return convertToHexFormats(input).replaceFirst(
            Regex("^(?:<#(?:[0-9A-Fa-f]{6})>|<reset>)", RegexOption.IGNORE_CASE),
            ""
        )
    }
}
