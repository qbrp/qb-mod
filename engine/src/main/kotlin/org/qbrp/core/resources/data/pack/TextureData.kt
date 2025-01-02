package org.qbrp.core.resources.data.pack

import com.google.gson.JsonParser
import org.qbrp.core.resources.data.Data
import org.qbrp.core.resources.units.TextureUnit
import java.io.File

data class TextureData(
    val texturePath: String = "" // Значение по умолчанию
) : Data(TextureUnit::class.java) {
    override fun toFile(): String = texturePath

    companion object {
        fun fromFile(file: File): TextureData {
            return TextureData(file.path)
        }
    }
}
