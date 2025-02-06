package org.qbrp.system.utils.format

import net.minecraft.text.OrderedText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import org.qbrp.core.Core

import java.util.regex.Pattern

object Format {

    private val HEX_PATTERN = Pattern.compile("&?#([A-Fa-f0-9]{6})")

    fun String.stripFormatting(): String {
        return stripAllFormatting(this)
    }

    fun stripAllFormatting(text: String): String {
        val strippedVanilla = text.replace(Regex("&[0-9A-FK-ORa-fk-or]"), "")
        val strippedHex = HEX_PATTERN.matcher(strippedVanilla).replaceAll("")
        return strippedHex
    }

    fun formatOrderedText(text: String): OrderedText {
        val colorized = colorize(text) // Преобразуем &-коды и HEX в §
        var currentStyle = Style.EMPTY
        val buffer = StringBuilder()
        val orderedParts = mutableListOf<OrderedText>()
        var i = 0

        while (i < colorized.length) {
            if (colorized[i] == '§') {
                if (i + 1 < colorized.length) {
                    val code = colorized[i + 1]

                    // Обработка HEX-цветов вида §x§R§R§G§G§B§B
                    if (code == 'x' && i + 13 < colorized.length) {
                        // Добавляем текущий буфер
                        if (buffer.isNotEmpty()) {
                            orderedParts.add(createOrderedSegment(buffer, currentStyle))
                            buffer.clear()
                        }

                        // Парсим HEX
                        val hex = buildString {
                            append(colorized[i + 3])
                            append(colorized[i + 5])
                            append(colorized[i + 7])
                            append(colorized[i + 9])
                            append(colorized[i + 11])
                            append(colorized[i + 13])
                        }
                        currentStyle = Style.EMPTY.withColor(TextColor.fromRgb(hex.toInt(16)))
                        i += 14
                    } else {
                        // Обработка ванильных кодов (например, §a)
                        val formatting = Formatting.byCode(code)
                        if (formatting != null) {
                            if (buffer.isNotEmpty()) {
                                orderedParts.add(createOrderedSegment(buffer, currentStyle))
                                buffer.clear()
                            }
                            currentStyle = currentStyle.withExclusiveFormatting(formatting)
                            i += 2
                        } else {
                            // Если код невалидный, добавляем символы как есть
                            buffer.append('§').append(code)
                            i += 2
                        }
                    }
                } else {
                    // Если после '§' нет символа, выходим из цикла
                    break
                }
            } else {
                // Обычный символ, добавляем в буфер
                buffer.append(colorized[i])
                i++
            }
        }

        if (buffer.isNotEmpty()) {
            orderedParts.add(createOrderedSegment(buffer, currentStyle))
        }

        return OrderedText.concat(*orderedParts.toTypedArray())
    }

    private fun createOrderedSegment(buffer: StringBuilder, style: Style): OrderedText {
        return OrderedText.styledForwardsVisitedString(buffer.toString(), style)
    }

    fun format(text: String): Text {
        val rootText = Text.literal("")
        var currentStyle = Style.EMPTY
        val buffer = StringBuilder()

        val matcher = HEX_PATTERN.matcher(text)
        var lastEnd = 0

        while (matcher.find()) {
            val start = matcher.start()
            val end = matcher.end()
            val hexColor = matcher.group(1)

            // Добавляем текст до начала совпадения
            if (start > lastEnd) {
                buffer.append(text.substring(lastEnd, start))
            }

            // Применяем текущий стиль к накопленному буферу
            if (buffer.isNotEmpty()) {
                rootText.append(Text.literal(buffer.toString()).setStyle(currentStyle))
                buffer.clear()
            }

            // Устанавливаем новый цвет
            currentStyle = Style.EMPTY.withColor(parseHexColor(hexColor))
            lastEnd = end
        }

        // Добавляем оставшийся текст
        if (lastEnd < text.length) {
            buffer.append(text.substring(lastEnd))
        }
        if (buffer.isNotEmpty()) {
            rootText.append(Text.literal(buffer.toString()).setStyle(currentStyle))
        }

        // Применяем ванильные коды (например, &a -> §a)
        return applyVanillaFormatting(rootText)
    }


    private fun parseHexColor(hex: String): TextColor {
        return try {
            TextColor.parse("#$hex".uppercase())!!
        } catch (e: NoSuchElementException) {
            TextColor.parse("#FFFFFF")!!.also {
                e.printStackTrace()
            }
        }
    }

    private fun applyVanillaFormatting(text: Text): Text {
        val formattedString = text.string
            .replace(Regex("&([0-9a-fk-or])"), "§$1")
        return Text.literal(formattedString).setStyle(text.style)
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

    fun String.formatMinecraft(): Text {
        return Format.format(this)
    }

    fun String.asIdentifier(): Identifier {
        return Identifier(Core.MOD_ID, this)
    }
}
