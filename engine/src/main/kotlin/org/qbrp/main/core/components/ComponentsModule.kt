package org.qbrp.main.core.components

import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.main.core.components.serialization.map.ComponentDtoMapper
import org.qbrp.main.core.events.EventPriority
import org.qbrp.main.core.events.on
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.engine.ModLoadedEvent

@Autoload(priority = Priorities.HIGHEST, both = true)
class ComponentsModule: EngineModule("components") {
    override fun getKoinModule(): Module = module {
        single { ComponentDtoMapper() }
        single<ComponentRegistry> { ComponentRegistryImpl(get(), get()) }
        single { ComponentRegistrar(get()) }
    }

    override fun onEnable() {
        val registry by lazy { getLocal<ComponentRegistry>() }
        on<ModLoadedEvent>(EventPriority.HIGHEST) {
            ComponentRegistryInitializationEvent.Companion.EVENT.invoker().onInitialized(registry)
        }
    }

}