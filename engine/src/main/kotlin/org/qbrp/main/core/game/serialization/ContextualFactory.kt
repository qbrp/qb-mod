package org.qbrp.main.core.game.serialization

interface ContextualFactory<T, C> {
    fun fromJson(json: String, context: C): T
    fun toJson(obj: T): String
}