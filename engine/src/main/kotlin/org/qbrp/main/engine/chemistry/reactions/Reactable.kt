package org.qbrp.main.engine.chemistry.reactions

import org.qbrp.main.engine.chemistry.agent.Agent
import org.qbrp.main.engine.chemistry.environment.Environment

interface Reactable {
    fun react(agent: Agent, ctx: ReactionContext, env: Environment)
}