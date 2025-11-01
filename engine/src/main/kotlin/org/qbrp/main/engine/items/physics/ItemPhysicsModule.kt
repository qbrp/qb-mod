package org.qbrp.main.engine.items.physics

import org.qbrp.main.core.components.ComponentRegistryInitializationEvent
import org.qbrp.main.core.components.extensions.loadJson
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.engine.items.model.ItemState
import org.qbrp.main.engine.chemistry.MaterialManager
import org.qbrp.main.engine.items.physics.debug.PhysicDebug

@Autoload(Priorities.HIGHEST)
class ItemPhysicsModule: EngineModule("item-physics") {
    override fun onEnable() {
        ComponentRegistryInitializationEvent.EVENT.register {
            it.register {
                componentClass(PhysicDebug::class)
                standartConstructor<ItemState> { PhysicDebug() }
            }
            it.register {
                componentClass(ItemPhysics::class)
                serializeConstructor<ItemPhysics.Data, ItemState, ItemPhysics> { builder, dto ->
                    val materialManager = MaterialManager()
                    val physics = ItemPhysics(builder.componentContext, materialManager)
                    materialManager.parent = physics
                    materialManager.loadJson(dto.materials, materialManager)
                    physics
                }
            }
        }
    }

}