package org.qbrp.main.core.components.serialization

interface ContextualSerializer<T, C> {
    fun fromJson(json: String, context: C): T
    fun toJson(obj: T): String
}