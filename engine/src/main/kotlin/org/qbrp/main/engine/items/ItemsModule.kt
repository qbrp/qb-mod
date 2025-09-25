package org.qbrp.main.engine.items

import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.dsl.module
import org.qbrp.deprecated.resources.data.config.ServerConfigData
import org.qbrp.main.core.Core
import org.qbrp.main.core.assets.prefabs.PrefabsAPI
import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.ComponentRegistryInitializationEvent
import org.qbrp.main.core.components.events.EventSupportersStorage
import org.qbrp.main.core.components.extensions.componentRegistrar
import org.qbrp.main.core.components.extensions.getOrAdd
import org.qbrp.main.core.components.objects.StateObject
import org.qbrp.main.core.events.EventPriority
import org.qbrp.main.core.events.on
import org.qbrp.main.core.events.onComponentEvent
import org.qbrp.main.core.events.withEventContext
import org.qbrp.main.core.game.saving.ServerStopSaver
import org.qbrp.main.core.game.saving.TimerSaver
import org.qbrp.main.core.game.storage.Storage
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.engine.items.service.ItemFactory
import org.qbrp.main.engine.items.service.ItemStorage
import org.qbrp.main.engine.items.service.ItemStateTicker
import org.qbrp.main.core.modules.GameModule
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.networking.sender.ServerPacketSender
import org.qbrp.main.core.networking.serialization.impl.GlobalComponentPacketDtoMapper
import org.qbrp.main.core.networking.serialization.mapping.SerializerKeyRegistry
import org.qbrp.main.core.storage.TableRegistry
import org.qbrp.main.core.storage.TableAccess
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.ModLoadedEvent
import org.qbrp.main.engine.containers.v2.api.ContainerService
import org.qbrp.main.engine.items.model.LeavePlayerInventorySaver
import org.qbrp.main.engine.items.service.ItemRepository
import org.qbrp.main.engine.items.components.meta.ItemLocationMediator
import org.qbrp.main.engine.items.components.meta.Meta
import org.qbrp.main.engine.items.networking.ItemSynchronizer
import org.qbrp.main.engine.items.event.ItemLoadEvent
import org.qbrp.main.engine.items.model.ItemState
import org.qbrp.main.engine.items.model.ServerItemObject
import org.qbrp.main.engine.items.service.ItemDeleteService
import org.qbrp.main.engine.items.service.ItemDeleteServiceImpl
import org.qbrp.main.engine.players.model.events.PlayerInitAfterJsonLoadEvent
import org.qbrp.main.engine.players.inventory.creative.PositionProxy
import org.qbrp.main.engine.players.model.events.PlayerInstantiateEvent
import org.qbrp.main.engine.players.model.service.ServerPlayerStorage

@Autoload(Priorities.ITEMS)
class ItemsModule: GameModule("items") {
    init {
        dependsOn { Core.isApiAvailable<PrefabsAPI>() }
        dependsOn { Engine.isApiAvailable<TableRegistry>() }
    }
    val itemPickupDisabled get() = get<ServerConfigData>().itemPickupEnabled

    companion object {
        const val PREFAB_CATEGORY = "item"
        const val TICK_RATE = 10
    }

    override fun onLoad() {
        get<PrefabsAPI>().registerPrefabCategory(PREFAB_CATEGORY)
        val storage = get<ItemStorage<ServerItemObject>>()
        val repo = get<ItemRepository>()
        val containerService by inject<ContainerService>()

        TimerSaver<ServerItemObject>("Items", 4000L * 40L)
            .run(storage, repo)
        ServerStopSaver<ServerItemObject>()
            .run(storage, repo)
        LeavePlayerInventorySaver(this)
            .run(storage, repo)

        ComponentRegistryInitializationEvent.EVENT.register {
            it.register { data<Meta>() }
            it.register { componentClass(ItemSynchronizer::class) }
            it.register { componentClass(ItemLocationMediator::class) }
            it.register { componentClass(PositionProxy::class) }
        }

        withEventContext {
            onComponentEvent<PlayerInstantiateEvent, ItemSynchronizer>(priority = EventPriority.LATEST) { event, component ->
                component.onPlayerInitialize(event.player)
            }

            on<ModLoadedEvent> {
                containerService.addProvider(storage)
            }

            onItemLoadEvent(get(), get(), get())
        }

        gameAPI.addWorldTickTask(getLocal<ItemStateTicker>())

        get<EventSupportersStorage>().addStorage(storage as Storage<StateObject<out ComponentManager>>)
    }

    fun onItemLoadEvent(
        packetSender: ServerPacketSender,
        mapper: GlobalComponentPacketDtoMapper,
        keyRegistry: SerializerKeyRegistry,
    ) {
        val playerStorage: ServerPlayerStorage by inject()
        val containerService: ContainerService by inject()
        ItemLoadEvent.EVENT.register { item ->
            item.getOrAdd {
                ItemSynchronizer(item.state.componentContext,
                    packetSender,
                    mapper,
                    keyRegistry,
                    playerStorage
                )
            }
            item.getOrAdd { ItemLocationMediator(item.state.componentContext, containerService) }
        }
    }

    override fun getKoinModule() = module {
        single { ItemStorage<ServerItemObject>() }
        single<TableAccess> { get<TableRegistry>().getTable("items")}
        single { ItemRepository(this@ItemsModule, get(), get(), get()) }
        single { ItemFactory(get()) }
        single { ItemStateTicker(get()) }
        single<ItemDeleteService> { ItemDeleteServiceImpl(get(), get(), get(), get()) }
    }
}