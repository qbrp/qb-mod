package org.qbrp.main.engine.physics.model.reactions

import org.qbrp.main.engine.physics.model.agent.Agent

interface ReactionContext {
    val strength: Double // ньютоны
    fun getPressure(agent: Agent): Double
}