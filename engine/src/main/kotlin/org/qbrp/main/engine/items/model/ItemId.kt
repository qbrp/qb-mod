package org.qbrp.main.engine.items.model

import kotlinx.serialization.Serializable
import org.qbrp.main.core.components.serialization.Identifiable

@JvmInline
@Serializable
value class ItemId(val value: String) : Identifiable {
    override val id: String
        get() = value
}