package org.qbrp.main.engine.containers

import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.main.core.components.extensions.componentRegistrar
import org.qbrp.main.core.components.serialization.map.ComponentDtoMapper
import org.qbrp.main.core.events.on
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.networking.serialization.SerializerRegistryId
import org.qbrp.main.core.networking.serialization.SerializersRegistry
import org.qbrp.main.engine.ModLoadedEvent
import org.qbrp.main.engine.containers.api.ContainerService
import org.qbrp.main.engine.containers.components.ContainedIn
import org.qbrp.main.engine.containers.components.Container
import org.qbrp.main.engine.containers.serialization.ContainerAnchorMapper
import org.qbrp.main.engine.containers.serialization.ContainerDto
import org.qbrp.main.engine.containers.system.ContainerCache
import org.qbrp.main.engine.containers.system.ContainerEntityStorage
import org.qbrp.main.engine.containers.system.ContainerSystem
import org.qbrp.main.engine.containers.system.ItemCache
import org.qbrp.main.engine.containers.system.ItemCommandBuffer
import org.qbrp.main.engine.containers.system.ItemCommandBufferImpl
import org.qbrp.main.engine.containers.system.TransferSystem

@Autoload(Priorities.ITEMS - 1)
class ContainersV2Module : EngineModule("containers-v2") {
    override fun getKoinModule(): Module = module {
        single { ContainerEntityStorage() }
        single { ItemCommandBufferImpl() }
        single<ItemCommandBuffer> { get<ItemCommandBufferImpl>() }
        single { TransferSystem(get()) }
        single<ContainerCache> { ItemCache(get()) }
        single { ContainerSystem(get(), get(), get(), get(), get(), get()) }
        single<ContainerService> { get<ContainerSystem>() }
        single { ContainerAnchorMapper(get()) }
    }

    override fun onEnable() {
        on<ModLoadedEvent>() {
            get<ComponentDtoMapper>().register(
                mapper = get<ContainerAnchorMapper>(),
                objClass = Container::class,
                dtoClass = ContainerDto::class
            )
            get<SerializersRegistry>().registerSerializer(SerializerRegistryId.ofComponent("Container"), ContainerDto::class)
        }

        componentRegistrar {
            build { data<ContainedIn>() }
            build { data<Container>(false) }
        }
    }
}