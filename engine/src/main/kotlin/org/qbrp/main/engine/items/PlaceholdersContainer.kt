package org.qbrp.main.engine.items

interface PlaceholdersContainer {
    val placeholders: MutableMap<String, String>
    fun substitutePlaceholders(text: String) =
        placeholders.entries.fold(text) { acc, (k, v) -> acc.replace("\${$k}", v) }
    fun String.substitutePlaceholders(text: String): String {
        return this@PlaceholdersContainer.substitutePlaceholders(this)
    }
}