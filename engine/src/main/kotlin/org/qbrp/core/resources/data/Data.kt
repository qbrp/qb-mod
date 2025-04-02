package org.qbrp.core.resources.data

import com.google.gson.GsonBuilder
import org.qbrp.core.resources.units.TextUnit

abstract class Data(@Transient var unit: Class<*> = TextUnit::class.java) {
    abstract fun toFile(): String

    companion object {
        val gson = GsonBuilder().setPrettyPrinting().create()
    }
}
