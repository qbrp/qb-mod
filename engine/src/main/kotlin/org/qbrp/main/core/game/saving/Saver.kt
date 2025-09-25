package org.qbrp.main.core.game.saving

import org.qbrp.main.core.components.serialization.Identifiable

fun interface Saver<T: Identifiable> {
    fun saveObject(obj: T)
}