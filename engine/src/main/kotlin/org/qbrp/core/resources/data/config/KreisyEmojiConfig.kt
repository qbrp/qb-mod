package org.qbrp.core.resources.data.config

import com.google.common.io.Files
import org.qbrp.core.resources.data.YamlData
import java.io.File

data class KreisyEmojiConfig(
    val emojis: Map<String, EmojiField>
) : YamlData() {
    override fun toFile(): String {
        throw UnsupportedOperationException()
    }

    data class EmojiField(val symbol: String, val image: String)

    companion object {
        fun fromFile(file: File): KreisyEmojiConfig {
            return mapper.readValue(file.readText(), KreisyEmojiConfig::class.java)
        }
    }
}