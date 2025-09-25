package org.qbrp.main.core.networking.serialization

import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.networking.serialization.impl.GlobalComponentPacketDtoMapper
import org.qbrp.main.core.networking.serialization.impl.MapperSerializerKeyRegistryImpl
import org.qbrp.main.core.networking.serialization.impl.ComponentPacketDtoMapperRegistry
import org.qbrp.main.core.networking.serialization.mapping.SerializerKeyRegistry

@Autoload(priority = Priorities.HIGHEST, both = true)
class PacketSerializationModule: EngineModule("packet-serialization") {
    override fun getKoinModule(): Module = module {
        single<SerializersRegistry> { SerializersRegistryImpl() }
        single { GlobalComponentPacketDtoMapper() }
        single<ComponentPacketDtoMapperRegistry> { get<GlobalComponentPacketDtoMapper>() }
        single<SerializerKeyRegistry> { MapperSerializerKeyRegistryImpl(listOf(get<GlobalComponentPacketDtoMapper>())) }
    }
}