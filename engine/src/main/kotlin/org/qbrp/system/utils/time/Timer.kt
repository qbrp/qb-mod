package org.qbrp.system.utils.time
import org.qbrp.system.utils.log.Loggers

class Timer(private val interval: Int, private val callback: () -> Unit) {
    private var tickCounter = 0
    private val logger = Loggers.get("timers")

    fun start(): Timer {
        if (this in TimerUpdater.timers) {
             logger.log("Попытка запустить таймер не удалась: уже запущен")
        } else {
            TimerUpdater.timers.add(this)
        }
        return this
    }

    fun update() {
        tickCounter++
        if (tickCounter >= interval) { tickCounter = 0; callback() }
    }

    fun hasReached(): Boolean = tickCounter >= interval
    fun reset() { tickCounter = 0 }
    fun getRemainingTicks(): Int = interval - tickCounter
    fun destroy() = TimerUpdater.timers.remove(this)

}
