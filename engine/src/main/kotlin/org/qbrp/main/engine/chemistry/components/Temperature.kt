package org.qbrp.main.engine.chemistry.components

import kotlinx.serialization.Serializable
import org.qbrp.main.core.components.serialization.DTO
import org.qbrp.main.engine.chemistry.ChemistryModule
import org.qbrp.main.engine.chemistry.agent.Agent
import org.qbrp.main.engine.chemistry.materials.MaterialProperty
import org.qbrp.main.engine.chemistry.materials.MaterialPropertyContext
import org.qbrp.main.engine.chemistry.reactions.Reactable
import org.qbrp.main.engine.chemistry.reactions.ReactionContext
import org.qbrp.main.engine.chemistry.environment.Environment
import kotlin.math.abs

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
        if (abs(deltaT) < 1e-6) return

        val m = material.proportionMass
        val c = specificHeat.toDouble()
        val dt = ChemistryModule.DELTA_TIME_SECONDS

        val dT = conductivity * deltaT * dt / (c * m)

        agent.temperature = T1 + dT
    }
}
