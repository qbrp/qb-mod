package org.qbrp.main.core.modules

import org.qbrp.main.core.Core
import org.qbrp.main.core.game.ComponentsRegistry
import org.qbrp.main.engine.Engine
import org.qbrp.main.core.game.GameAPI

open class GameModule(name: String): QbModule(name) {
    init {
        dependsOn { Core.isApiAvailable<GameAPI>() }
    }
    protected val gameAPI = Engine.getAPI<GameAPI>()!!
}