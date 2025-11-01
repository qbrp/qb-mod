package org.qbrp.main.engine.containers.components

import org.qbrp.main.engine.text.literal

data class ContainerDisplayName(val value: String) {
    companion object {
        fun default() = ContainerDisplayName("Контейнер")
    }
    fun asLiteral() = literal(value)
}