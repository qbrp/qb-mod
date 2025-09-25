package org.qbrp.main.core.game

import net.minecraft.server.world.ServerWorld
import org.qbrp.main.core.events.on
import org.qbrp.main.core.events.withEventContext
import org.qbrp.main.core.game.loop.Tick
import org.qbrp.main.core.game.loop.GameTicker
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.engine.ModLoadedEvent

@Autoload(both = true, priority = Priorities.HIGHEST)
class GameTickerModule : EngineModule("game-ticker"), org.qbrp.main.core.game.GameTicker {
    override fun onEnable() = withEventContext {
        on<ModLoadedEvent> {
            getLocal<GameTicker>().startTicking()
        }
    }

    override fun getKoinModule() = innerWithApi({
        single<org.qbrp.main.core.game.GameTicker> { this@GameTickerModule }
    }, {
        scoped { GameTicker() }
    })


    override fun addTickTask(tickable: Tick<Unit>) {
        getLocal<GameTicker>().addTickTask(tickable)
    }

    override fun addWorldTickTask(tickable: Tick<ServerWorld>) {
        getLocal<GameTicker>().addWorldTickTask(tickable)
    }
}