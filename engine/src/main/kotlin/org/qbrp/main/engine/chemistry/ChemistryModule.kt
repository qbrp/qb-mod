package org.qbrp.main.engine.chemistry


import org.koin.core.component.get
import org.koin.dsl.module
import org.qbrp.main.core.components.ComponentRegistryInitializationEvent
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.engine.chemistry.components.Friction
import org.qbrp.main.engine.chemistry.components.Temperature
import org.qbrp.main.engine.chemistry.environment.ComponentEnvironmentProvider
import org.qbrp.main.engine.chemistry.environment.EnvironmentProvider
import org.qbrp.main.engine.chemistry.materials.Material
import org.qbrp.main.engine.chemistry.environment.WorldEnv

@Autoload
class ChemistryModule : EngineModule("chemistry") {
    companion object {
        const val TICK_RATE = 10
        const val DELTA_TIME_SECONDS: Double = TICK_RATE.toDouble() / 20
    }

    override fun getKoinModule() = module {
        single { WorldEnv() }
        single<EnvironmentProvider> { ComponentEnvironmentProvider(get()) }
    }

    override fun onEnable() {
        val worldEnv = get<WorldEnv>()
        ComponentRegistryInitializationEvent.EVENT.register {
            it.register {
                componentClass(Temperature::class)
                serializeConstructor<Temperature.Data, Material, Temperature> { builder, dto ->
                    Temperature(builder.ctx, dto.specificHeat, dto.conductivity)
                }
            }
            it.register { data<Friction>() }
        }
    }
}