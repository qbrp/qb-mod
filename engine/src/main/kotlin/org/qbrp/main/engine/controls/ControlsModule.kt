package org.qbrp.main.engine.controls

import org.koin.core.component.get
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.main.core.events.EventPriority
import org.qbrp.main.core.events.on
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.engine.controls.system.lookup.LookupManager
import org.qbrp.main.engine.controls.model.InteractionNodeComposer
import org.qbrp.main.engine.controls.model.InteractionService
import org.qbrp.main.engine.controls.system.InteractionCallSystem
import org.qbrp.main.engine.controls.system.InteractionSystem
import org.qbrp.main.engine.controls.system.InteractionSystemImpl
import org.qbrp.main.engine.controls.system.modes.InteractionModeSystem
import org.qbrp.main.engine.players.model.PlayerDestroyEvent
import org.qbrp.main.engine.players.model.PlayerInstantiateEvent

@Autoload(Priorities.LOWEST)
class ControlsModule : EngineModule("controls") {
    override fun getKoinModule(): Module = module {
        single { InteractionService(get()) }
        single { LookupManager(get(), get()) }
        single { InteractionNodeComposer(get()) }
        single<InteractionSystem> {
            InteractionSystemImpl(
                InteractionCallSystem(),
            )
        }
        single { InteractionModeSystem() }
    }

    override fun onEnable() {
        val interactionService = get<InteractionService>()

        on<PlayerInstantiateEvent>(priority = EventPriority.LATEST) {
            interactionService.onPlayerJoin(player)
        }

        on<PlayerDestroyEvent> {
            interactionService.onPlayerLeave(player.uuid)
        }
    }
}