package org.qbrp.main.engine.items.model

import kotlinx.serialization.Serializable
import org.qbrp.main.core.game.serialization.Identifiable
import org.qbrp.main.core.game.model.State

@Serializable
class ItemJsonField(override val id: Long, val state: State): Identifiable