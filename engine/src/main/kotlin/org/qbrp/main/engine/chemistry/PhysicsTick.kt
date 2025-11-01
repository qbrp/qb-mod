package org.qbrp.main.engine.chemistry

import org.qbrp.main.engine.chemistry.environment.Environment

interface PhysicsTick {
    fun tickPhysics(env: Environment, parentEnvironment: Environment?)
}