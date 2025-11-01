package org.qbrp.main.core.fabricmc.player

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class Username(val value: String) {
    init { require(value.none { it.isWhitespace() }) }
    override fun toString(): String = value
}