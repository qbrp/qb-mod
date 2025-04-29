package org.qbrp.system.modules

import org.qbrp.core.game.ComponentsRegistry
import org.qbrp.engine.Engine
import org.qbrp.engine.game.GameAPI

open class GameModule(name: String): QbModule(name) {
    init {
        dependsOn { Engine.isApiAvailable<GameAPI>() }
    }
    protected val gameAPI = Engine.getAPI<GameAPI>()!!

    open fun registerComponents(registry: ComponentsRegistry) = Unit

}