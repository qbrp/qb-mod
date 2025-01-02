package org.qbrp.core.resources.data

import com.google.gson.GsonBuilder
import org.qbrp.core.resources.units.ContentUnit

abstract class Data(@Transient val unit: Class<*> = ContentUnit::class.java) {
    abstract fun toFile(): String

    companion object {
        val gson = GsonBuilder().setPrettyPrinting().create()
    }
}
