package org.qbrp.main.core.modules

import org.qbrp.main.core.Core
import org.qbrp.main.engine.Engine
import org.qbrp.main.core.game.GameTicker

open class GameModule(name: String): EngineModule(name) {
    init {
        dependsOn { Core.isApiAvailable<GameTicker>() }
    }
    protected val gameAPI = Engine.getAPI<GameTicker>()!!
}