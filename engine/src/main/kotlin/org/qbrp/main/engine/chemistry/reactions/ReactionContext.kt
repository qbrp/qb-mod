package org.qbrp.main.engine.chemistry.reactions

import org.qbrp.main.engine.chemistry.agent.Agent

interface ReactionContext {
    val strength: Double // ньютоны
    fun getPressure(agent: Agent): Double
}