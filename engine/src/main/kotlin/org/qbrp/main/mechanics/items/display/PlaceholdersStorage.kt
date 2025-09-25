package org.qbrp.main.mechanics.items.display

interface PlaceholdersStorage {
    val placeholders: MutableMap<String, String>
    fun substitutePlaceholders(text: String) =
        placeholders.entries.fold(text) { acc, (k, v) -> acc.replace("\${$k}", v) }
    fun String.substitutePlaceholders(text: String): String {
        return this@PlaceholdersStorage.substitutePlaceholders(this)
    }
}