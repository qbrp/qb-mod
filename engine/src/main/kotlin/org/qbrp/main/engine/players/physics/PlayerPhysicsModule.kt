package org.qbrp.main.engine.players.physics

import org.qbrp.main.core.components.ComponentRegistryInitializationEvent
import org.qbrp.main.core.components.construct.ComponentBuilderOperator.Companion.default
import org.qbrp.main.core.events.on
import org.qbrp.main.core.events.withEventContext
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.events.PlayerInitBeforeJsonLoadEvent

@Autoload(Priorities.PLAYERS - 1)
class PlayerPhysicsModule: EngineModule("player-physics") {
    override fun onEnable() {
        ComponentRegistryInitializationEvent.EVENT.register {
            it.register {
                componentClass(PlayerAgent::class)
                standartConstructor<ServerPlayerObject> { PlayerAgent(it.componentContext) }
            }
            it.register {
                data<Strength>()
                standartConstructor<ServerPlayerObject> { Strength(0.5) }
            }
        }

        withEventContext {
            on<PlayerInitBeforeJsonLoadEvent> {
                it.player.default<PlayerAgent>()
                it.player.default<Strength>()
            }
        }
    }
}