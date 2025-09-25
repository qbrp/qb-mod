package org.qbrp.main.engine.controls.entity.display

import org.qbrp.main.core.components.serialization.Identifiable
import org.qbrp.main.engine.items.ModelId

@JvmInline
value class IconItemModelId(val value: String): Identifiable, ModelId {
    override val id: String get() = value
    override val modelId: String get() = value
}