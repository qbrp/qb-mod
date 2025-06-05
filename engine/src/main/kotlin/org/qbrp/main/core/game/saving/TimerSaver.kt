package org.qbrp.main.core.game.saving

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import org.qbrp.main.core.game.serialization.Identifiable
import org.qbrp.main.core.game.storage.Storage
import kotlin.concurrent.fixedRateTimer

class TimerSaver<T: Identifiable>(val threadName: String, val period: Long, val initialDelay: Long = 0): AutomaticSaver<T> {
    override fun run(
        storage: Storage<T>,
        saver: Saver<T>
    ) {
        fixedRateTimer("qbrp/Autosave/$threadName", true, initialDelay, period) {
            storage.getAll().forEach { saver.saveObject(it) }
        }
    }
}