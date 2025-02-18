package org.qbrp.engine.time

import org.qbrp.core.resources.data.config.ServerConfigData
import org.qbrp.engine.Engine
import org.qbrp.engine.time.TimeModule.Time
import org.qbrp.system.utils.log.Loggers
import kotlin.math.round

class PeriodManager(
    private val worldTimeManager: WorldTimeManager,
    private val notifications: TimeNotifications,
    private val config: ServerConfigData.Time
) {
    val logger = Loggers.get("time")

    private val periods: List<Period> = config.periods
    private var currentPeriodIndex = 0
    private val completedPeriods: MutableList<Period> = mutableListOf()

    val currentPeriod: Period?
        get() = periods.getOrNull(currentPeriodIndex)

    fun nextPeriod() {
        // Добавляем завершённый период в список выполненных
        currentPeriod?.let { completedPeriods.add(it) }
        currentPeriodIndex++
        if (currentPeriod != null) {
            println(
                "Новый период: ${currentPeriod?.name}. Длительность: ${currentPeriod?.duration} минут, " +
                        "Прошедшее время: ${currentPeriod?.elapsedTimeMinutes}"
            )
        } else {
            println("Все периоды завершены.")
        }
    }

    fun getDayCycleGameTime() = periods.sumOf { it.duration }
    fun getDayCycleTickTime() = periods.sumOf { it.endTimeTicks - it.startTimeTicks }
    fun getDayCycleRpTime() = periods.sumOf { it.getRpDuration() } + config.rpTimeOffset

    fun getGameTime(): Int {
        val timeFromCompleted = completedPeriods.sumOf { it.duration }
        val period = currentPeriod ?: return 1.also { println("Период не найден") }
        return timeFromCompleted + period.getElapsedTime()
    }

    fun getRpTime(): Int {
        return try {
            val completedRpTime = completedPeriods.sumOf { it.getRpDuration() }
            val result = completedRpTime + (currentPeriod?.getRpTime() ?: 0) + config.rpTimeOffset
            result.toInt() % getDayCycleRpTime()
        } catch (e: Exception) {
            e.printStackTrace()
            1
        }
    }

    fun setRpTime(rpTime: Int) {
        // Сбрасываем историю выполненных периодов и состояние elapsedTimeMinutes
        completedPeriods.clear()
        periods.forEach { it.elapsedTimeMinutes = 0 }
        currentPeriodIndex = 0

        var accumulatedRpTime = 0 + config.rpTimeOffset // Аккумулируем RP-время для определения текущего периода

        while (currentPeriodIndex < periods.size) {
            val period = periods[currentPeriodIndex]
            val periodRpDuration = period.getRpDuration()

            if (accumulatedRpTime + periodRpDuration > rpTime) {
                val rpTimeInPeriod = rpTime - accumulatedRpTime
                period.elapsedTimeMinutes = (rpTimeInPeriod * period.duration) / periodRpDuration
                break
            }

            accumulatedRpTime += periodRpDuration
            completedPeriods.add(period)
            currentPeriodIndex++
        }

        // Проверяем, если RP-время выходит за пределы цикла
        if (rpTime >= getDayCycleRpTime()) {
            println("RP-время выходит за пределы цикла. Сбрасываем до начала.")
            setRpTime(rpTime % getDayCycleRpTime()) // Рекурсивно сбрасываем до корректного значения
        }
    }

    var tickCounter = 0
    private val tickStepDuration = 1200

    fun handleTick() {
        val period = currentPeriod ?: return
        if (period.duration - period.elapsedTimeMinutes <= 1 && currentPeriodIndex == periods.size - 1) {
            Engine.timeModule.enabled = false
            setRpTime(config.rpTimeOffset)
            return
        }
        if (tickCounter++ > tickStepDuration) {
            period.incrementElapsedTime()
            if (period.isFinished()) {
                nextPeriod()
            }
            handleNotification()
            worldTimeManager.setTickTime(period.getTickTime())
            logger.log("<<${period.name}>> РП: ${Time.minutesToTime(getRpTime())}. В минутах: ${getGameTime()}. В тиках: ${period.getTickTime()}")
            tickCounter = 0
        }
    }

    var lastSentHour = -1
    fun handleNotification() {
        val rpHours = getRpTime() / 60
        // Проверяем, если наступил нужный интервал, и ещё не отправляли уведомление
        if ((rpHours % config.doFrequency) == 0 && rpHours != lastSentHour) {
            notifications.broadcastTimeDo(
                getRpTime(),
                currentPeriod?.name ?: "Конец"
            )
            // Обновляем последний отправленный интервал
            lastSentHour = rpHours
        }
    }

}
