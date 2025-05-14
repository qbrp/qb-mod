package org.qbrp.engine.time

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import org.koin.core.component.get
import org.koin.dsl.module
import org.qbrp.core.mc.registry.CommandsRepository
import org.qbrp.engine.Engine
import org.qbrp.engine.chat.addons.BroadcasterAPI
import org.qbrp.engine.chat.addons.groups.ChatGroupsAPI
import org.qbrp.engine.time.config.PeriodsConfig
import org.qbrp.engine.time.config.TimeConfig
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.QbModule

/*
!!!ШТОБ НЕ ПУТАТЬСЯ!!!
TickTime - время игры в тиках
GameTime - сколько прошло минут с начала сессии
RpTime - игровое время в 24-часовом формате
 */

@Autoload(priority = 0)
class TimeModule(): QbModule("time"), TimeAPI {
    private lateinit var periodManager: PeriodManager
    private lateinit var worldTimeManager: WorldTimeManager

    init {
        dependsOn { Engine.isApiAvailable<BroadcasterAPI>() }
        dependsOn { Engine.isApiAvailable<ChatGroupsAPI>() }
        createModuleFileOnInit()
    }

    override fun getKoinModule() = module {
        single { createConfig(TimeConfig()) }
        single { createConfig(PeriodsConfig()) }
        single { WorldTimeManager(get()) }
        single { TimeNotifications() }
        single { PeriodManager(get(), get()) }
    }

    override fun getAPI(): TimeAPI = this

    override fun onLoad() {
        CommandsRepository.add(TimeCommands(getAPI()))

        periodManager = get<PeriodManager>()
        worldTimeManager = get<WorldTimeManager>()

        once {
            ServerTickEvents.END_SERVER_TICK.register {
                ifEnabled { if (enabled) periodManager.handleTick() }
            }
        }

        allowDynamicLoading()
        allowDynamicActivation()
    }

    override fun onEnable() {
        setCycleEnabled(true)
    }

    override fun onDisable() {
        setCycleEnabled(false)
    }

    override fun getRpTime() = periodManager.getRpTime()

    override fun getGameTime() = periodManager.getGameTime()

    override fun getTickTime() = worldTimeManager.getTickTime()

    override  fun getCurrentPeriod() = periodManager.currentPeriod

    override fun setRpTime(time: Int) = periodManager.setRpTime(time)

    override fun getFormattedRpTime() = TimeUtils.minutesToTime(periodManager.getRpTime())

    override fun setCycleEnabled(enabled: Boolean) { this.enabled = enabled }

    override fun broadcastTime(time: Int, name: String) = get<TimeNotifications>().broadcastTimeDo(time, name)

    var enabled = true
}