package org.qbrp.main.core.util.save

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import org.qbrp.main.core.components.serialization.Identifiable
import org.qbrp.main.core.util.storage.Storage
import kotlin.concurrent.fixedRateTimer

fun <T : Identifiable> runServerStopSaver(
    storage: Storage<T>,
    saver: Saver<T>
) {
    ServerLifecycleEvents.SERVER_STOPPING.register {
        storage.getAll().forEach { saver.saveObject(it) }
    }
}

fun <T : Identifiable> runPeriodicSaver(
    storage: Storage<T>,
    saver: Saver<T>,
    scope: String,
    period: Long
) {
    fixedRateTimer("Engine $scope Autosave", true, 0L, period) {
        storage
            .getAll()
            .toList()
            .forEach {
                try {
                    saver.saveObject(it)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
    }
}