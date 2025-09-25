package org.qbrp.main.engine.physics.dynamics.simulation

interface PhysicsSimulation {
    var debugLog: Boolean
    fun step(delta: Float)
}