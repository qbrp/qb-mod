package org.qbrp.deprecated.resources.data
import org.qbrp.deprecated.resources.units.TextUnit
import java.io.File

data class StringData(
    var string: String = ""
) : Data(unit = TextUnit::class.java) {
    override fun toFile(): String = string
    companion object {
        fun fromFile(file: File): StringData {
            return StringData(file.readText())
        }
    }
}
