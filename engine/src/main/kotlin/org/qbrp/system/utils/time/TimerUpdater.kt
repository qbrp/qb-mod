package org.qbrp.system.utils.time

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import java.util.concurrent.CopyOnWriteArrayList

object TimerUpdater {
    var timers = CopyOnWriteArrayList<Timer>()

    fun update() = timers.forEach { timer -> timer.update() }

    fun registerCycle() {
        ServerTickEvents.END_WORLD_TICK.register { server ->
            update()
        }
    }
}