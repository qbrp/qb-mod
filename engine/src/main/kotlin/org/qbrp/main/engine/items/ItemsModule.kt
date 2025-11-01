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
import org.qbrp.main.core.components.extensions.getOrAdd
import org.qbrp.main.core.components.objects.StateObject
import org.qbrp.main.core.events.on
import org.qbrp.main.core.util.storage.Storage
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.engine.items.service.ItemFactory
import org.qbrp.main.engine.items.service.ItemStorage
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.storage.TableRegistry
import org.qbrp.main.core.storage.TableAccess
import org.qbrp.main.core.util.save.runPeriodicSaver
import org.qbrp.main.core.util.save.runServerStopSaver
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.ModLoadedEvent
import org.qbrp.main.engine.containers.api.ContainerService
import org.qbrp.main.engine.items.service.ItemRepository
import org.qbrp.main.engine.items.components.meta.ItemLocationMediator
import org.qbrp.main.engine.items.components.meta.Meta
import org.qbrp.main.engine.items.event.ItemLoadEvent
import org.qbrp.main.engine.items.model.ServerItemObject
import org.qbrp.main.engine.items.service.ItemDeleteService
import org.qbrp.main.engine.items.service.ItemDeleteServiceImpl
import org.qbrp.main.engine.items.service.runLeavePlayerItemSaver
import org.qbrp.main.engine.players.inventory.PositionProxy

@Autoload(Priorities.ITEMS)
class ItemsModule : EngineModule("items") {
    val itemPickupDisabled get() = get<ServerConfigData>().itemPickupEnabled

    init {
        dependsOn { Core.isApiAvailable<PrefabsAPI>() }
        dependsOn { Engine.isApiAvailable<TableRegistry>() }
    }

    override fun onLoad() {
        get<PrefabsAPI>().registerPrefabCategory(PREFAB_CATEGORY)
        val storage = get<ItemStorage<ServerItemObject>>()
        val repo = get<ItemRepository>()
        val containerService by inject<ContainerService>()

        runServerStopSaver(storage, repo)
        runPeriodicSaver(storage, repo, "Items", 4000L * 40)
        runLeavePlayerItemSaver(storage, repo)

        ComponentRegistryInitializationEvent.EVENT.register {
            it.register { data<Meta>() }
            it.register { componentClass(ItemLocationMediator::class) }
            it.register { componentClass(PositionProxy::class) }
        }

        on<ModLoadedEvent> {
            containerService.addProvider(storage)
        }

        onItemLoadEvent()

        get<EventSupportersStorage>().addStorage(storage as Storage<StateObject<out ComponentManager>>)
    }

    fun onItemLoadEvent() {
        val containerService: ContainerService by inject()
        ItemLoadEvent.EVENT.register { item ->
            item.getOrAdd { ItemLocationMediator(item.state.componentContext, containerService) }
        }
    }

    override fun getKoinModule() = module {
        single { ItemStorage<ServerItemObject>() }
        single<TableAccess> { get<TableRegistry>().getTable("items")}
        single { ItemRepository(this@ItemsModule, get(), get(), get()) }
        single { ItemFactory(get()) }
        single<ItemDeleteService> { ItemDeleteServiceImpl(get(), get(), get(), get()) }
    }

    companion object {
        const val PREFAB_CATEGORY = "item"
    }
}