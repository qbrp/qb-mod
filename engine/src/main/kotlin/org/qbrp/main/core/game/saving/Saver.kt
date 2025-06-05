package org.qbrp.main.core.game.saving

import org.qbrp.main.core.game.serialization.Identifiable

fun interface Saver<T: Identifiable> {
    fun saveObject(obj: T)
}