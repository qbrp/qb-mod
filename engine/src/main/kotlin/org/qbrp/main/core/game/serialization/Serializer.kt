package org.qbrp.main.core.game.serialization

interface Serializer<T> {
    fun toJson(t: T): String
}