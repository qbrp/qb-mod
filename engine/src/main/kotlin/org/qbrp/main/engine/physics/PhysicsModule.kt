package org.qbrp.main.engine.physics


import org.koin.core.component.get
import org.koin.dsl.module
import org.qbrp.main.core.components.ComponentRegistryInitializationEvent
import org.qbrp.main.core.game.GameTicker
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.engine.physics.components.Friction
import org.qbrp.main.engine.physics.components.Temperature
import org.qbrp.main.engine.physics.environment.ComponentEnvironmentProvider
import org.qbrp.main.engine.physics.environment.EnvironmentProvider
import org.qbrp.main.engine.physics.model.materials.Material
import org.qbrp.main.engine.physics.environment.WorldEnv

@Autoload
class PhysicsModule: EngineModule("physics") {
    companion object {
        const val TICK_RATE = 10
        const val DELTA_TIME_SECONDS: Double = TICK_RATE.toDouble() / 20
    }

    override fun getKoinModule() = module {
        single { WorldEnv() }
        single<EnvironmentProvider> { ComponentEnvironmentProvider(get()) }
    }

    override fun onEnable() {
        get<GameTicker>().addWorldTickTask(get<WorldEnv>())
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