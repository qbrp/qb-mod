package org.qbrp.deprecated.resources.data

import com.google.gson.GsonBuilder
import org.qbrp.deprecated.resources.units.TextUnit

@Deprecated("Использовать ассеты")
abstract class Data(@Transient var unit: Class<*> = TextUnit::class.java) {
    abstract fun toFile(): String

    companion object {
        val gson = GsonBuilder().setPrettyPrinting().create()
    }
}
