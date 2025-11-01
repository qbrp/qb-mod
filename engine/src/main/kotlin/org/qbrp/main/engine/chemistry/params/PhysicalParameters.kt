package org.qbrp.main.engine.chemistry.params

import org.qbrp.main.engine.chemistry.environment.Environment

interface PhysicalParameters {
    val mass: Double

    fun getWeight(gravity: Double): Double {
        return mass * gravity
    }

    fun getWeight(environment: Environment): Double {
        return getWeight(environment.gravity)
    }
}