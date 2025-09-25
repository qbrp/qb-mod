package org.qbrp.main.engine.containers.v2

import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.main.core.components.extensions.componentRegistrar
import org.qbrp.main.core.components.serialization.map.ComponentDtoMapper
import org.qbrp.main.core.events.on
import org.qbrp.main.core.game.GameTicker
import org.qbrp.main.core.mc.server.context.withCoreContext
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.networking.serialization.SerializerRegistryId
import org.qbrp.main.core.networking.serialization.SerializersRegistry
import org.qbrp.main.engine.ModLoadedEvent
import org.qbrp.main.engine.containers.v2.api.ContainerService
import org.qbrp.main.engine.containers.v2.components.ContainedIn
import org.qbrp.main.engine.containers.v2.components.Container
import org.qbrp.main.engine.containers.v2.components.ContainerAnchor
import org.qbrp.main.engine.containers.v2.serialization.ContainerAnchorMapper
import org.qbrp.main.engine.containers.v2.serialization.ContainerDto
import org.qbrp.main.engine.containers.v2.system.ContainerCache
import org.qbrp.main.engine.containers.v2.system.ContainerEntityStorage
import org.qbrp.main.engine.containers.v2.system.ContainerSystem
import org.qbrp.main.engine.containers.v2.system.ItemCache
import org.qbrp.main.engine.containers.v2.system.ItemCommandBuffer
import org.qbrp.main.engine.containers.v2.system.ItemCommandBufferImpl
import org.qbrp.main.engine.containers.v2.system.TransferSystem

@Autoload(Priorities.ITEMS - 1)
class ContainersV2Module: EngineModule("containers-v2") {
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

    override fun onEnable() = withCoreContext {
        val gameTicker: GameTicker by inject()

        on<ModLoadedEvent>() {
            val containerSystem: ContainerSystem by inject()
            gameTicker.addTickTask { containerSystem.update() }
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