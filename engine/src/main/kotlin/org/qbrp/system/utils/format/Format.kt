package org.qbrp.system.utils.format

import icyllis.modernui.graphics.Color
import icyllis.modernui.text.Spannable
import icyllis.modernui.text.SpannableString
import icyllis.modernui.text.style.ForegroundColorSpan
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

    private fun getSpannableString(input: String, stripColorCodes: Boolean = true): SpannableString {
        val colorMap = mapOf(
            '0' to Color.rgb(0, 0, 0),       // Черный
            '1' to Color.rgb(0, 0, 170),     // Темно-синий
            '2' to Color.rgb(0, 170, 0),     // Темно-зеленый
            '3' to Color.rgb(0, 170, 170),   // Темно-голубой
            '4' to Color.rgb(170, 0, 0),     // Темно-красный
            '5' to Color.rgb(170, 0, 170),   // Темно-фиолетовый
            '6' to Color.rgb(255, 170, 0),   // Золотой
            '7' to Color.rgb(170, 170, 170), // Серый
            '8' to Color.rgb(85, 85, 85),    // Темно-серый
            '9' to Color.rgb(170, 170, 255), // Голубой
            'a' to Color.rgb(85, 255, 85),   // Зеленый
            'b' to Color.rgb(85, 255, 255),  // Голубой
            'c' to Color.rgb(255, 85, 85),   // Красный
            'd' to Color.rgb(255, 85, 255),  // Розовый
            'e' to Color.rgb(255, 255, 85),  // Желтый
            'f' to Color.rgb(255, 255, 255)  // Белый
        )

        val builder = StringBuilder() // Буфер для очищенного текста
        val spannableIndices = mutableListOf<Triple<Int, Int, Int>>() // (start, end, color)

        var lastColor: Int? = null
        var startIndex = 0
        var currentIndex = 0

        while (currentIndex < input.length) {
            if (input[currentIndex] == '&' && currentIndex + 1 < input.length) {
                val code = input[currentIndex + 1]
                if (code == 'r') { // Сброс цвета
                    if (lastColor != null) {
                        spannableIndices.add(Triple(startIndex, builder.length, lastColor!!))
                    }
                    lastColor = null
                    currentIndex += 2
                } else if (colorMap.containsKey(code)) { // Новый цвет
                    if (lastColor != null) {
                        spannableIndices.add(Triple(startIndex, builder.length, lastColor!!))
                    }
                    lastColor = colorMap[code]
                    currentIndex += 2
                } else {
                    builder.append(input[currentIndex])
                    currentIndex++
                }
            } else {
                builder.append(input[currentIndex])
                currentIndex++
            }

            if (lastColor != null) {
                startIndex = builder.length
            }
        }

        if (lastColor != null) {
            spannableIndices.add(Triple(startIndex, builder.length, lastColor!!))
        }

        val cleanedString = if (stripColorCodes) builder.toString() else input
        val spannable = SpannableString(cleanedString)

        for ((start, end, color) in spannableIndices) {
            spannable.setSpan(ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return spannable
    }


    fun String.stripFormatting(): String {
        return stripAllFormatting(this)
    }

    fun stripAllFormatting(text: String): String {
        val strippedVanilla = text.replace(Regex("&[0-9A-FK-ORa-fk-or]"), "")
        val strippedHex = HEX_PATTERN.matcher(strippedVanilla).replaceAll("")
        return strippedHex
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

    fun String.formatMinecraft(): Text {
        return Format.format(this)
    }

    fun String.formatAsSpans(): SpannableString {
        return getSpannableString(this)
    }

    fun String.asIdentifier(): Identifier {
        return Identifier(Core.MOD_ID, this)
    }
}
