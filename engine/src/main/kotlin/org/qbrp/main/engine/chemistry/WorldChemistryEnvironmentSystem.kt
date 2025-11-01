package org.qbrp.main.engine.chemistry

import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.engine.chemistry.agent.Agent
import org.qbrp.main.engine.chemistry.environment.WorldEnv
import org.qbrp.main.engine.items.model.ServerItemObject
import org.qbrp.main.engine.items.service.ItemStorage

class WorldChemistryEnvironmentSystem(
    private val env: WorldEnv,
    private val itemStorage: ItemStorage<ServerItemObject>
) {
    private var tickCounter = 0

    fun update() {
        tickCounter++
        if (tickCounter % ChemistryModule.TICK_RATE != 0) return

        val agents = itemStorage.getAll()
            .mapNotNull { it.get<Agent>() }

        for (agent in agents) {
            val air = env.getAir(agent)
            agent.react(air, env.getAirReactionCtx(), env)
            agent.tickPhysics(env, null)
        }
    }
}