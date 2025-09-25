package org.qbrp.main.engine.items.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.qbrp.main.core.components.serialization.Identifiable
import org.qbrp.main.core.components.state.StateJsonField

@Serializable
class ItemJsonField(override val id: String, val state: StateJsonField): Identifiable