package org.qbrp.main.engine.time

import org.koin.core.component.get
import org.qbrp.main.core.mc.commands.CommandsAPI
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import org.koin.core.component.get
import org.qbrp.main.core.mc.commands.CommandsModule
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.chat.addons.BroadcasterAPI
import org.qbrp.main.engine.chat.addons.groups.ChatGroupsAPI
import org.qbrp.main.engine.time.config.PeriodsConfig
import org.qbrp.main.engine.time.config.TimeConfig
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.QbModule

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

    override fun getKoinModule() = inner<TimeAPI>(this) {
        scoped { createConfig(TimeConfig()) }
        scoped { createConfig(PeriodsConfig()) }
        scoped { WorldTimeManager(get()) }
        scoped { TimeNotifications(get()) }
        scoped { PeriodManager(
            get(),
            get(),
            get(),
            get<PeriodsConfig>().periods)
        }
    }

    override fun onLoad() {
        get<CommandsAPI>().add(TimeCommands(get()))

        periodManager = getLocal<PeriodManager>()
        worldTimeManager = getLocal<WorldTimeManager>()

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

    override fun broadcastTime(time: Int, name: String) = getLocal<TimeNotifications>().broadcastTimeDo(time, name)

    var enabled = true
}