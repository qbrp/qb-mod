package org.imperial_hell.ihCommands

import net.minecraft.text.Text

import java.util.regex.Pattern

object Format {

    private val HEX_PATTERN = Pattern.compile("&?#([A-Fa-f0-9]{6})")

    fun format(text: String): Text {
        val coloredText = colorize(text)
        return Text.literal(coloredText)
    }

    private fun colorize(text: String): String {
        return translateAlternateColorCodes('&', translateHexColors(text))
    }

    private fun translateHexColors(text: String): String {
        return HEX_PATTERN.matcher(text).replaceAll { matcher ->
            val hexColor = matcher.group(1)
            buildString {
                append("§x")
                for (char in hexColor) {
                    append('§').append(char)
                }
            }
        }
    }

    private fun translateAlternateColorCodes(altColorChar: Char, text: String): String {
        return text.replace(Regex("(?i)$altColorChar([0-9A-FK-ORa-fk-or])")) { match ->
            "§${match.groupValues[1]}"
        }
    }

    fun stripColor(text: String): String {
        return text.replace(Regex("§[0-9A-FK-ORa-fk-or]"), "")
    }

    fun String.toMinecraft(): Text {
        return Format.format(this)
    }
}
