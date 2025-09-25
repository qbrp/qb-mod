package org.qbrp.main.core.game.saving

import org.qbrp.main.core.components.serialization.Identifiable
import org.qbrp.main.core.game.storage.Storage

interface AutomaticSaver<T: Identifiable> {
    fun run(storage: Storage<T>, saver: Saver<T>)
}