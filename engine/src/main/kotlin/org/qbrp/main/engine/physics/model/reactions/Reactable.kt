package org.qbrp.main.engine.physics.model.reactions

import org.qbrp.main.engine.physics.model.agent.Agent
import org.qbrp.main.engine.physics.environment.Environment

interface Reactable {
    fun react(agent: Agent, ctx: ReactionContext, env: Environment)
}