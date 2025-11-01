package org.qbrp.main.engine.chemistry.reactions

import org.qbrp.main.engine.chemistry.agent.Agent
import org.qbrp.main.engine.chemistry.environment.Environment

class AtmosphereReactionContext(val environment: Environment): ReactionContext {
    override val strength: Double = 0.0
    override fun getPressure(agent: Agent): Double = environment.pressure
}