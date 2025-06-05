package org.qbrp.main.core.game.serialization

fun interface Deserializer<T> {
    fun fromJson(json: String): T
}