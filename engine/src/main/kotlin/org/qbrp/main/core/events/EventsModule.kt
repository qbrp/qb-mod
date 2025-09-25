package org.qbrp.main.core.events

import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.main.core.components.events.EventSupportersStorage
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.modules.EngineModule

@Autoload(Priorities.HIGHEST + 1, both = true)
class EventsModule: EngineModule("events") {
    override fun getKoinModule(): Module = module {
        single<EventBus> { EventManager() }
        single<EventSupportersStorage> { EventSupportersStorage() }
        single { DefaultEventContext(get(), get()) }
        single<EventContext> { get<DefaultEventContext>() }
    }
}