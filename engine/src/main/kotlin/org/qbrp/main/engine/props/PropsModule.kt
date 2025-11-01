package org.qbrp.main.engine.props

import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.qbrp.main.core.components.ComponentRegistryInitializationEvent
import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.items.ItemsModule
import org.qbrp.main.engine.items.model.ServerItemObject
import org.qbrp.main.engine.items.event.ItemLoadEvent
import org.qbrp.main.engine.items.service.ItemStorage
import org.qbrp.main.engine.physics.PhysicsWorldModule
import org.qbrp.main.engine.props.components.Dimensions
import org.qbrp.main.engine.props.components.PropManager
import org.qbrp.main.engine.props.components.SpawnedProp

@Autoload(Priorities.LOWEST)
class PropsModule() : EngineModule("props"), WorldPropManager {
    init {
        dependsOn { Engine.isModuleEnabled<ItemsModule>() }
        dependsOn { Engine.isModuleEnabled<PhysicsWorldModule>() }
    }

    private val itemStorage by inject<ItemStorage<ServerItemObject>>()
    override val props: Collection<SpawnedProp> get() = itemStorage.getAll().flatMap { it.filterIsInstance<SpawnedProp>() }

    override fun getKoinModule(): Module = innerWithApi({
        single<WorldPropManager> { this@PropsModule }
    }, {})

    override fun onEnable() {
        get<ChunkLoadSchedulerSystem>().addPositionTracker(PropTrackingPositions(get()))

        ComponentRegistryInitializationEvent.EVENT.register {
            it.register {
                componentClass(PropManager::class)
            }
            it.register {
                data<Dimensions>()
            }
            it.register {
                componentClass(SpawnedProp::class)
            }
        }

        ItemLoadEvent.EVENT.register { item ->
            item.state.add(PropManager(item.state.componentContext, get(), get()))
        }
    }

    companion object {
        const val PROP_DIMENSIONS_COMPONENT = "Dimensions#Prop"
    }
}