package org.qbrp.main.core.components

import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.main.core.components.serialization.map.ComponentDtoMapper
import org.qbrp.main.core.events.EventPriority
import org.qbrp.main.core.events.on
import org.qbrp.main.core.events.withEventContext
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.engine.ModLoadedEvent

@Autoload(both = true)
class ComponentsModule: EngineModule("components") {
    override fun getKoinModule(): Module = module {
        single { ComponentDtoMapper() }
        single<ComponentRegistry> { ComponentRegistryImpl(get(), get()) }
        single { ComponentRegistrar(get()) }
    }

    override fun onEnable() = withEventContext {
        val registry = getLocal<ComponentRegistry>()
        on<ModLoadedEvent>(EventPriority.HIGHEST) {
            ComponentRegistryInitializationEvent.Companion.EVENT.invoker().onInitialized(registry)
        }
    }

}