package org.qbrp.main.core.game.saving

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import org.qbrp.main.core.game.serialization.Identifiable
import org.qbrp.main.core.game.storage.Storage

class ServerStopSaver<T: Identifiable>(): AutomaticSaver<T> {
    override fun run(
        storage: Storage<T>,
        saver: Saver<T>
    ) {
        ServerLifecycleEvents.SERVER_STOPPING.register {
            storage.getAll().forEach { saver.saveObject(it) }
        }
    }
}