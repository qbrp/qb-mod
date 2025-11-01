package org.qbrp.main.engine.players.model

import kotlinx.serialization.Serializable
import org.qbrp.main.core.components.serialization.Identifiable
import org.qbrp.main.core.components.state.StateJsonField

@Serializable
data class PlayerJsonField(
    override val id: String,
    val state: StateJsonField,
    var name: String
) : Identifiable