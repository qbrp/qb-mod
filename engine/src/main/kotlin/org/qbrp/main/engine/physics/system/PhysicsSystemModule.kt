package org.qbrp.main.engine.physics.system

import org.koin.core.module.Module
import org.qbrp.main.core.Core
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.engine.physics.system.jolt.JoltBodyFactory
import org.qbrp.main.engine.physics.system.jolt.JoltPhysicsSimulation
import org.qbrp.main.engine.physics.system.jolt.buildJoltPhysicsSystem
import org.qbrp.main.engine.physics.system.jolt.buildUpJoltSimulation
import org.qbrp.main.engine.physics.system.jolt.loadJoltNativeLibrary
import org.qbrp.main.engine.physics.system.service.BodyFactory
import org.qbrp.main.engine.physics.system.service.ManipulatorSystem
import org.qbrp.main.engine.physics.system.service.PhysicsSimulation
import org.qbrp.main.engine.physics.system.service.WorldBodyManager

@Autoload(priority = Priorities.HIGHEST, both = true)
class PhysicsSystemModule : EngineModule("physics-system") {
    override fun getKoinModule(): Module = innerWithApi({
        single<PhysicsSimulation> { getLocal<JoltPhysicsSimulation>() }
        single<ManipulatorSystem> { getLocal<ManipulatorSystem>() }
        single<BodyFactory> { getLocal<JoltBodyFactory>() }
        single<WorldBodyManager>() { getLocal<JoltPhysicsSimulation>() }
    }, {
        scoped { JoltBodyFactory(get()) }
        if (!Core.isClient) {
            scoped { ManipulatorSystem() }
        }
        scoped { buildJoltPhysicsSystem(BuiltinPhysicsConfiguration) }
        scoped { buildUpJoltSimulation(get(), system = get()) }
    })

    override fun onEnable() {
        loadJoltNativeLibrary()
    }

    companion object {
        const val TICK_DELTA = 0.05f
    }
}