package org.qbrp.main.engine.physics.components

import kotlinx.serialization.Serializable
import org.qbrp.main.core.components.serialization.DTO
import org.qbrp.main.engine.physics.PhysicsModule
import org.qbrp.main.engine.physics.model.agent.Agent
import org.qbrp.main.engine.physics.model.materials.MaterialProperty
import org.qbrp.main.engine.physics.model.materials.MaterialPropertyContext
import org.qbrp.main.engine.physics.model.reactions.Reactable
import org.qbrp.main.engine.physics.model.reactions.ReactionContext
import org.qbrp.main.engine.physics.environment.Environment

class Temperature(
    ctx: MaterialPropertyContext,
    val specificHeat: Int,          // удельная теплоемкость, дж на кг
    val conductivity: Double = 0.1  // коэффициент теплопередачи
) : MaterialProperty(ctx), Reactable {

    @Serializable data class Data(val specificHeat: Int, val conductivity: Double = 0.1): DTO
    override val dto: DTO?
        get() = Data(specificHeat, conductivity)


    override fun react(
        rAgent: Agent,
        ctx: ReactionContext,
        env: Environment
    ) {
        val T1 = agent.temperature.toDouble()
        val T2 = rAgent.temperature.toDouble()
        val deltaT = T2 - T1
        if (kotlin.math.abs(deltaT) < 1e-6) return

        val m = material.proportionMass
        val c = specificHeat.toDouble()
        val dt = PhysicsModule.DELTA_TIME_SECONDS

        val dT = conductivity * deltaT * dt / (c * m)

        agent.temperature = T1 + dT
    }
}
