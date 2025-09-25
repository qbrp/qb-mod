package org.qbrp.main.core.components.serialization

import org.qbrp.main.core.dataformats.StringId

interface Identifiable {
    val id: String
    val stringId: StringId get() = StringId(id)
}