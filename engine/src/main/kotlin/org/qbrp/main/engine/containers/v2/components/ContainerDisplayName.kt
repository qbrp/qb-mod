package org.qbrp.main.engine.containers.v2.components

import net.minecraft.text.Text
import org.qbrp.main.core.utils.format.Format.formatMm
import org.qbrp.main.engine.text.literal

data class ContainerDisplayName(val value: String) {
    companion object {
        fun default() = ContainerDisplayName("Контейнер")
    }
    fun asLiteral() = literal(value)
}