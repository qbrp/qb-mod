package org.qbrp.core.game.serialization

import org.qbrp.core.game.model.State

open class ObjectJsonField(
    val id: Long,
    val state: State,
    val eternal: Boolean) {
}