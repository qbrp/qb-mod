package org.qbrp.main.engine.physics.environment
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Vec3d
import org.qbrp.main.core.game.loop.Tick
import org.qbrp.main.core.game.storage.Storage
import org.qbrp.main.engine.physics.AgentEntity
import org.qbrp.main.engine.physics.PhysicsModule
import org.qbrp.main.engine.physics.PhysicsTick
import org.qbrp.main.engine.physics.model.agent.Agent
import org.qbrp.main.engine.physics.model.reactions.AtmosphereReactionContext

open class WorldEnv(
    override var temperature: Double = 300.0,
    override val gravity: Double = EARTH_GRAVITY,
    override var pressure: Double = EARTH_PRESSURE,
) : Environment, Tick<ServerWorld> {
    companion object {
        const val EARTH_GRAVITY = 9.81
        const val EARTH_PRESSURE = 101325.0
    }

    fun getAirReactionCtx() = AtmosphereReactionContext(this)

    private val providers: MutableList<Storage<AgentEntity>> = mutableListOf()

    private var tickCounter = 0
    override fun tick(serverWorld: ServerWorld) {
        tickCounter++
        if (tickCounter % PhysicsModule.TICK_RATE != 0) return

        val agents = providers
            .flatMap { it.getAll() }

        agents.forEach {
            if (it.agent != null) {
                val air = getAir(it.agent!!)
                it.agent!!.react(air, getAirReactionCtx(), this)
            }
            if (it is PhysicsTick) it.tickPhysics(this, null)
        }
    }


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
