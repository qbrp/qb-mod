package org.qbrp.engine.chat.core.system

import net.minecraft.text.LiteralTextContent
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import org.qbrp.system.utils.format.Format.stripFormatting

object TextTagsTransformer {
    fun getTextTags(text: String): List<String> {
        val regex = Regex("<([^<>]+)>") // Ищет текст между < и >
        return regex.findAll(text).map { it.groupValues[1] }.toList()
    }

    fun replaceTagsWithFormat(text: String, name: String, replacer: (tag: String, value: String?) -> String): String {
        val regex = Regex("<([^<>]+)>")
        return regex.replace(text) { matchResult ->
            val tagContent = matchResult.groupValues[1]
            val parts = tagContent.split(":", limit = 2)

            val tag = parts[0]
            if (tag == name) {
                val value = if (parts.size > 1) parts[1] else null
                replacer(tag, value?.replace(""""""", ""))
            } else {
                matchResult.value
            }
        }
    }

    fun replaceWordsInText(text: Text, replacements: Map<String, String>): Text {
        val newText: MutableText = if (text.content is LiteralTextContent) {
            var newContent = (text.content as LiteralTextContent).string
            replacements.forEach { (target, replacement) ->
                newContent = newContent.replace(target, replacement)
            }
            Text.literal(newContent).setStyle(text.style)
        } else {
            text.copy()
        }

        // Рекурсивно обрабатываем дочерние элементы (siblings)
        text.siblings.forEach { sibling ->
            newText.append(replaceWordsInText(sibling, replacements))
        }
        return newText
    }

}