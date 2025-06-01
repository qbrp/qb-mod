package org.qbrp.main.core.utils.log

class LoggerKey(val categories: List<String>) {

    override fun toString(): String {
        return categories.joinToString("/") { it.replaceFirstChar { char -> char.uppercaseChar() } }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LoggerKey) return false
        return categories == other.categories
    }

    override fun hashCode(): Int {
        return categories.hashCode()
    }
}