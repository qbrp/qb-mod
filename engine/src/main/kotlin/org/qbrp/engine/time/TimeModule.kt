// TimeModule.kt (адаптер для интеграции)
package org.qbrp.engine.time

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.server.MinecraftServer
import org.qbrp.core.game.registry.CommandsRepository
import org.qbrp.core.resources.data.config.ServerConfigData
import org.qbrp.core.resources.data.config.ServerConfigData.Time
import org.qbrp.system.utils.log.Loggers

/*
!!!ШТОБ НЕ ПУТАТЬСЯ!!!
TickTime - время игры в тиках
GameTime - сколько прошло минут с начала сессии
RpTime - игровое время в 24-часовом формате
 */


class TimeModule(server: MinecraftServer, config: ServerConfigData.Time) {
    private val worldTimeManager = WorldTimeManager(server)
    private val notifications = TimeNotifications(config)
    private val periodManager = PeriodManager(worldTimeManager,notifications, config)
    init {
        CommandsRepository.add(TimeCommands())
    }

    companion object Time {
        fun roundMinutesToNearestHalfHour(minutes: Int): Int {
            val remainder = minutes % 60

            return when {
                remainder < 15 -> minutes - remainder
                remainder < 45 -> minutes - remainder + 30
                else -> minutes - remainder + 60
            }
        }

        fun minutesToTime(minutes: Int): String {
            val hours = minutes / 60
            val mins = minutes % 60
            return String.format("%02d:%02d", hours, mins)
        }

    }

    fun load() {
        ServerTickEvents.END_SERVER_TICK.register {
            if (enabled) periodManager.handleTick()
        }
        Loggers.get("time").success("TimeModule загружен")
    }

    val API = Api()
    var enabled = false

    inner class Api {
        fun getRpTime() = periodManager.getRpTime()
        fun getGameTime() = periodManager.getGameTime()
        fun getTickTime() = worldTimeManager.getTickTime()
        fun getCurrentPeriod() = periodManager.currentPeriod
        fun setRpTime(time: Int) = periodManager.setRpTime(time)
        fun getFormattedRpTime() = minutesToTime(periodManager.getRpTime())
    }
}