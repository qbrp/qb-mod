package org.qbrp.main.core.components.serialization

interface Serializer<T> {
    fun toJson(t: T): String
}