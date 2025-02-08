package org.qbrp.engine.chat.core.system

import org.qbrp.system.utils.format.Format.stripFormatting

object TextTagsTransformer {
    fun getTextTags(text: String): List<String> {
        val regex = Regex("<([^<>]+)>") // Ищет текст между < и >
        return regex.findAll(text).map { it.groupValues[1] }.toList()
    }

    fun replaceTagsWithFormat(text: String, name: String, replacer: (tag: String, value: String?) -> String): String {
        val regex = Regex("<([^<>]+)>")
        return regex.replace(text.stripFormatting()) { matchResult ->
            val tagContent = matchResult.groupValues[1]
            val parts = tagContent.split(":", limit = 2)

            val tag = parts[0]
            if (tag.stripFormatting() == name) {
                val value = if (parts.size > 1) parts[1] else null
                replacer(tag, value)
            } else {
                matchResult.value
            }
        }
    }
}