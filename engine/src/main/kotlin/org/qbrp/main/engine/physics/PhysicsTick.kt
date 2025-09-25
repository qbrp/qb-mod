package org.qbrp.main.engine.physics

import org.qbrp.main.engine.physics.environment.Environment

interface PhysicsTick {
    fun tickPhysics(env: Environment, parentEnvironment: Environment?)
}