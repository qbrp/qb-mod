package org.qbrp.main.engine.physics.model.reactions

import org.qbrp.main.engine.physics.model.agent.Agent
import org.qbrp.main.engine.physics.environment.Environment

class AtmosphereReactionContext(val environment: Environment): ReactionContext {
    override val strength: Double = 0.0
    override fun getPressure(agent: Agent): Double = environment.pressure
}