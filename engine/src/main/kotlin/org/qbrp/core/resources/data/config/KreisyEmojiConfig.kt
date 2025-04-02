package org.qbrp.core.resources.data.config

import org.qbrp.core.resources.data.YamlData

data class KreisyEmojiConfig(val emojis: Map<String, EmojiField>): YamlData() {
    override fun toFile(): String {
        throw UnsupportedOperationException()
    }

    data class EmojiField(val symbol: String, val image: String)

    companion object {
        fun fromFile(text: String): KreisyEmojiConfig {
            return mapper.readValue(text, KreisyEmojiConfig::class.java)
        }
    }
}