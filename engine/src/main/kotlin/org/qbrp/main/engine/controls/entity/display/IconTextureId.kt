package org.qbrp.main.engine.controls.entity.display

import org.qbrp.main.core.components.serialization.Identifiable

@JvmInline
value class IconTextureId(val value: String): Identifiable {
    override val id: String get() = value
}