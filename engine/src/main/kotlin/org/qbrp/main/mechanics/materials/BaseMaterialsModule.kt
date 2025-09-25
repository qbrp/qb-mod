package org.qbrp.main.mechanics.materials

import org.koin.core.component.get
import org.qbrp.main.core.components.ComponentRegistry
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.engine.physics.model.MaterialManager
import org.qbrp.main.engine.physics.model.materials.Material
import org.qbrp.main.engine.physics.model.materials.MaterialDTO
import org.qbrp.main.engine.physics.model.materials.MaterialInstantiateContext

@Autoload
class BaseMaterialsModule: EngineModule("base-materials") {
    override fun onEnable() {
        get<ComponentRegistry>().apply {
            registerMaterial { AbsPlastic(it) }
            registerMaterial { BrainrotGas(it) }
            registerMaterial { Copper(it) }
            registerMaterial { Glass(it) }
            registerMaterial { CarbonSteel(it) }
        }
    }

    inline fun <reified T: Material> ComponentRegistry.registerMaterial(crossinline b: (MaterialInstantiateContext) -> T) {
        register {
            componentClass(T::class)
            serializeConstructor<MaterialDTO, MaterialManager, T> { builder, state ->
                b(builder.materialCtx(state.proportion))
            }
        }
    }

}