package org.qbrp.main.engine.physics.system.service

import kotlinx.coroutines.Job
import org.koin.core.component.KoinComponent
import org.qbrp.main.core.events.on
import org.qbrp.main.engine.physics.system.PhysicsSystemEvents
import org.qbrp.main.engine.physics.system.PhysicsSystemModule

interface PhysicsSimulation {
    var debugLog: Boolean
    val isRunning: Boolean
    fun step(delta: Float = PhysicsSystemModule.TICK_DELTA, callback: () -> Unit = {}): Job
    fun isOnThread(): Boolean
    fun isBodiesEditable(): Boolean
}

fun PhysicsSimulation.assertIsEditable() {
    assert(isOnThread()) { "Операции возможны только в потоке симуляции" }
    assert(isBodiesEditable()) { "Операции возможны только после завершения шага симуляции" }
}

fun KoinComponent.everyPhysicsTick(r: () -> Unit = {}) {
    on<PhysicsSystemEvents.Tick>() { r() }
}