package org.qbrp.main.engine.props

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.qbrp.main.core.components.ComponentRegistryInitializationEvent
import org.qbrp.main.core.components.construct.ComponentBuilderOperator.Companion.default
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.networking.serialization.SerializersRegistry
import org.qbrp.main.core.networking.serialization.impl.ComponentPacketDtoMapperRegistry
import org.qbrp.main.core.networking.serialization.mapping.registerDataComponentMapper
import org.qbrp.main.core.networking.serialization.serializerId
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.items.ItemsModule
import org.qbrp.main.engine.items.model.ServerItemObject
import org.qbrp.main.engine.items.event.ItemLoadEvent
import org.qbrp.main.engine.items.model.ItemState
import org.qbrp.main.engine.items.service.ItemStorage
import org.qbrp.main.engine.physics.dynamics.PhysicDynamicsModule
import org.qbrp.main.engine.physics.dynamics.tracking.PositionTracker
import org.qbrp.main.engine.props.components.Dimensions
import org.qbrp.main.engine.props.components.PropManager
import org.qbrp.main.engine.props.components.SpawnedProp
import org.qbrp.main.engine.props.networking.PropTransformSynchronizer
import org.qbrp.main.engine.props.world.WorldPropManager

@Autoload(Priorities.LOWEST)
class PropsModule() : EngineModule("props"), WorldPropManager {
    init {
        dependsOn { Engine.isModuleEnabled<ItemsModule>() }
        dependsOn { Engine.isModuleEnabled<PhysicDynamicsModule>() }
    }
    companion object {
        const val PROP_DIMENSIONS_COMPONENT = "Dimensions#Prop"
    }

    private val itemStorage by inject<ItemStorage<ServerItemObject>>()
    override val props: Collection<SpawnedProp> get() = itemStorage.getAll().filterIsInstance<SpawnedProp>()

    override fun getKoinModule(): Module = innerWithApi({
        single<WorldPropManager> { this@PropsModule }
    }, {
        scoped { PropTransformSynchronizer(get(), get(), get()) }
    })

    override fun onEnable() {
        val synchronizer = getLocal<PropTransformSynchronizer>()
        get<PositionTracker>().addPositionTracker(PropTrackingPositions(get()))

        ServerTickEvents.END_SERVER_TICK.register {
            synchronizer.syncProps()
        }

        get<SerializersRegistry>().registerSerializer<Dimensions>(Dimensions::class.serializerId(), Dimensions.serializer())
        get<ComponentPacketDtoMapperRegistry>().registerDataComponentMapper<Dimensions>()

        ComponentRegistryInitializationEvent.EVENT.register {
            it.register {
                componentClass(PropManager::class)
                standartConstructor<ItemState> {
                    PropManager(it.componentContext, get(), get())
                }
            }
            it.register {
                data<Dimensions>()
            }
            it.register {
                componentClass(SpawnedProp::class)
            }
        }

        ItemLoadEvent.EVENT.register { item ->
            item.state.default<PropManager>()
        }
    }
}