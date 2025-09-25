package org.qbrp.main.core.components.serialization

fun interface Deserializer<T> {
    fun fromJson(json: String): T
}