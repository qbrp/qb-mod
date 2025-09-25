package org.qbrp.main.core.utils.format

import com.google.gson.JsonParser
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.minecraft.text.OrderedText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import net.minecraft.util.Identifier
import org.qbrp.main.core.Core

import java.util.regex.Pattern

object Format {
    private val HEX_PATTERN = Pattern.compile("&\\{#?([A-Fa-f0-9]{6})}")

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
                buffer.append(text
                    .substring(lastEnd, start)
                    .replace("&", "§"))
            }
            // Применяем текущий стиль к накопленному буферу
            if (buffer.isNotEmpty()) {
                rootText.append(Text.literal(buffer.toString()
                    .replace("&", "§"))
                    .setStyle(currentStyle))
                buffer.clear()
            }

            // Устанавливаем новый цвет, используя HEX-код
            currentStyle = Style.EMPTY.withColor(parseHexColor(hexColor))
            lastEnd = end
        }

        // Добавляем оставшийся текст
        if (lastEnd < text.length) {
            buffer.append(text
                .substring(lastEnd)
                .replace("&", "§"))
        }
        if (buffer.isNotEmpty()) {
            rootText.append(Text.literal(buffer.toString().replace("&", "§")).setStyle(currentStyle))
        }

        return rootText
    }

    private fun parseHexColor(hex: String): TextColor {
        return try {
            TextColor.parse("#$hex".uppercase())!!.also {
            }
        } catch (e: NoSuchElementException) {
            e.printStackTrace()
            TextColor.parse("#FFFFFF")!!
        }
    }

    fun String.stripFormatting(): String {
        return stripAllFormatting(this)
    }

    @Deprecated("Использовать .asMiniMessage")
    fun String.formatMinecraft(): Text {
        return Format.format(this)
    }

    fun String.miniMessage(): String {
        val component = MiniMessage.miniMessage().deserialize(this)
        return GsonComponentSerializer.gson().serialize(component)
    }

    fun String.formatMm(): Text {
        val text = this
            .replace("§", "&")
            .replace("&0", "<black>")
            .replace("&1", "<dark_blue>")
            .replace("&2", "<dark_green>")
            .replace("&3", "<dark_aqua>")
            .replace("&4", "<dark_red>")
            .replace("&5", "<dark_purple>")
            .replace("&6", "<gold>")
            .replace("&7", "<gray>")
            .replace("&8", "<dark_gray>")
            .replace("&9", "<blue>")
            .replace("&a", "<green>")
            .replace("&b", "<aqua>")
            .replace("&c", "<red>")
            .replace("&d", "<light_purple>")
            .replace("&e", "<yellow>")
            .replace("&f", "<white>")
            .replace("&r", "<reset>")
            .replace("&l", "<bold>")
            .replace("&o", "<italic>")

        val component = MiniMessage.miniMessage().deserialize(text)
        val jsonObject = JsonParser.parseString(GsonComponentSerializer.gson().serialize(component)).asJsonObject

        return Text.Serializer.fromJson(jsonObject)!!
    }


    fun String.asIdentifier(): Identifier {
        return Identifier(Core.MOD_ID, this)
    }
}
