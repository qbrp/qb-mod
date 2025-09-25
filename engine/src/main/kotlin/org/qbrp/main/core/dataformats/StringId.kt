package org.qbrp.main.core.dataformats

import org.qbrp.main.core.components.serialization.Identifiable

@JvmInline
value class StringId(val value: String): Identifiable {
    override val id: String get() = value
}