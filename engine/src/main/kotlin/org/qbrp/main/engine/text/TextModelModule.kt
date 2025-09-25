package org.qbrp.main.engine.text

import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.networking.serialization.SerializersRegistry
import org.qbrp.main.engine.text.map.TextMapperRegistrar
import org.qbrp.main.engine.text.map.TextMappingRegisterContext
import org.qbrp.main.engine.text.map.TextMappingRegistry

@Autoload(Priorities.HIGHEST - 1, both = true)
class TextModelModule: EngineModule("mcText-model"), TextMappingRegisterContext {
    override fun getKoinModule(): Module = module {
        single { TextMappingRegistry() }
    }

    override val mapperRegistry: TextMappingRegistry by inject()
    override val serializersRegistry: SerializersRegistry by inject()
    override fun onEnable() {
        with(this) {
            TextMapperRegistrar().registerAll()
        }
    }
}