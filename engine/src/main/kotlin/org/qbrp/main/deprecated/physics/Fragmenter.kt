package org.qbrp.main.deprecated.physics

import kotlinx.serialization.Serializable
import net.minecraft.util.math.Vec3d
import org.qbrp.main.core.components.serialization.DTO
import org.qbrp.main.engine.items.physics.debug.DebugInfo
import org.qbrp.main.engine.chemistry.PhysicsTick
import org.qbrp.main.engine.chemistry.components.Fragments
import org.qbrp.main.engine.chemistry.agent.Agent
import org.qbrp.main.engine.chemistry.materials.MaterialProperty
import org.qbrp.main.engine.chemistry.materials.MaterialPropertyContext
import org.qbrp.main.engine.chemistry.reactions.Reactable
import org.qbrp.main.engine.chemistry.reactions.ReactionContext
import org.qbrp.main.engine.chemistry.environment.Environment
import kotlin.math.abs
import kotlin.random.Random

/**
 * Уничтожает агента при условиях сильного удара или температуры, разделяя его на фрагменты.
 * Фрагменты предоставляются внешним компонентом.
 */
@Deprecated("Не использовать")
class Fragmenter(
    ctx: MaterialPropertyContext,
    val criticalTemperature: Int? = null,
    val criticalTemperatureDelta: Double? = null,
    val criticalPressureDelta: Double? = null,
    val criticalStress: Double? = null,
    val stressDissipation: Double = 0.1
) : MaterialProperty(ctx), PhysicsTick, Reactable, DebugInfo {

    @Serializable data class Data(val criticalTemperature: Int?,
                                            val criticalTemperatureDelta: Double? = null,
                                            val criticalPressureDelta: Double? = null,
                                            val criticalStress: Double? = null,
                                            val stressDissipation: Double = 0.1): DTO
    override val dto: DTO get() = Data(criticalTemperature, criticalTemperatureDelta, criticalPressureDelta, criticalStress, stressDissipation)

    private var previousTemperature: Double? = null
    private var thermalStress: Double = 0.0

    override fun react(agent: Agent, ctx: ReactionContext, environment: Environment) {
        val pressure = ctx.getPressure(agent)
        val temp = previousTemperature ?: 0.0

        val pressureExceeded = criticalPressureDelta != null && pressure > criticalPressureDelta
        val tempExceeded = criticalTemperature != null && temp > criticalTemperature
        val stressExceeded = criticalStress != null && thermalStress > criticalStress

        if (tempExceeded || stressExceeded || pressureExceeded) {
            val impulse = calculateImpulse(agent.mass, temp)
            fragment(impulse, environment)
            thermalStress = 0.0
        }
    }

    override fun tickPhysics(env: Environment, parentEnvironment: Environment?) {
        val currentTemp = agent.temperature.toDouble()
        val tempChange = previousTemperature?.let { currentTemp - it } ?: 0.0

        // Накопление теплового стресса по абсолютному перепаду температуры
        if (abs(tempChange) > 0.0) {
            thermalStress += abs(tempChange)
        }

        val deltaExceeded = criticalTemperatureDelta != null && abs(tempChange) > criticalTemperatureDelta
        if (deltaExceeded) {
            val impulse = calculateImpulse(agent.mass.toDouble(), currentTemp)
            fragment(impulse, env)
        }

        // Рассеивание стресса
        thermalStress *= (1.0 - stressDissipation).coerceIn(0.0, 1.0)
        previousTemperature = currentTemp
    }

    fun fragment(impulse: Double, environment: Environment) {
        val fragments = external?.filterIsInstance<Fragments>()?.flatMap { it.fragments }
        fragments?.forEach {
            environment.launch(agent, it, randomDirection(), impulse)
        }
    }

    private fun randomDirection(): Vec3d {
        val x = Random.nextDouble(-1.0, 1.0)
        val y = Random.nextDouble(-1.0, 1.0)
        val z = Random.nextDouble(-1.0, 1.0)
        return Vec3d(x, y, z).normalize()
    }

    //кг * мс / с
    private fun calculateImpulse(mass: Double, temperature: Double): Double {
//        val specificHeat = agentMaterials.get<Temperature>()?.specificHeat ?: 100
//        val deltaT = maxOf(0.0, temperature - baseTemp)
//        val energy = specificHeat * mass * deltaT
//
//        val impulse = sqrt(2 * mass * energy)
        return 0.0
    }

    override fun getInfo(): String {
        return  "Критические значения:" + "<newline>" +
                "<red>-</red> Температура: <red>${criticalTemperature} K</red>" + "<newline>" +
                "<red>-</red> Термальный стресс: <red>${criticalStress}</red>" + "<newline>" +
                "<red>-</red> Дельта температуры: <red>${criticalTemperatureDelta} К</red>" + "<newline>" +
                "<red>-</red> Дельта давления: <red>${criticalPressureDelta} Па</red>" + "<newline>" +
                "Рассеивание стресса: <blue>${stressDissipation}</blue>" + "<newline>" +
                "Стресс: <blue>${thermalStress}</blue>"
    }
}
