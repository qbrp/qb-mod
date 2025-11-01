package org.qbrp.main.engine.chemistry.environment
import net.minecraft.util.math.Vec3d
import org.qbrp.main.engine.chemistry.agent.Agent
import org.qbrp.main.engine.chemistry.reactions.AtmosphereReactionContext

open class WorldEnv(
    override var temperature: Double = 300.0,
    override val gravity: Double = EARTH_GRAVITY,
    override var pressure: Double = EARTH_PRESSURE,
) : Environment {
    companion object {
        const val EARTH_GRAVITY = 9.81
        const val EARTH_PRESSURE = 101325.0
    }

    fun getAirReactionCtx() = AtmosphereReactionContext(this)

    override fun getAir(forAgent: Agent): Agent {
        return Air(this.temperature, forAgent.volume)
    }

    override fun createAgent(agent: Agent, pos: Vec3d) {
        TODO("Not yet implemented")
    }

    override fun launch(
        from: Agent,
        agent: Agent,
        direction: Vec3d,
        impulse: Double
    ) {
        TODO("Not yet implemented")
    }

    override fun detach(
        from: Agent,
        agent: Agent
    ) {
        TODO("Not yet implemented")
    }
}
