package org.qbrp.main.engine.physics.model.params

import kotlinx.serialization.Serializable
import org.qbrp.main.core.components.serialization.DTO
import org.qbrp.main.engine.physics.components.Friction
import org.qbrp.main.engine.physics.environment.Environment

interface PhysicalParameters {
    val mass: Double
    val averageFriction: Friction

    fun getWeight(gravity: Double): Double {
        return mass * gravity
    }

    fun getWeight(environment: Environment): Double {
        return getWeight(environment.gravity)
    }

    @Serializable
    class Data(val mass: Double, val averageFriction: Friction): DTO
}