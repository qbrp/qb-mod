package org.qbrp.main.core.game.saving

import org.qbrp.main.core.components.serialization.Identifiable
import org.qbrp.main.core.game.storage.Storage
import kotlin.concurrent.fixedRateTimer

class TimerSaver<T: Identifiable>(val threadName: String, val period: Long, val initialDelay: Long = 0): AutomaticSaver<T> {
    override fun run(
        storage: Storage<T>,
        saver: Saver<T>
    ) {
        fixedRateTimer("qbrp/Autosave/$threadName", true, initialDelay, period) {
            try {
                storage
                    .getAll()
                    .toList()
                    .forEach { saver.saveObject(it) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}