package org.qbrp.main.engine.physics.environment

import org.qbrp.main.engine.physics.model.agent.Agent

interface Atmosphere {
    fun getAir(forAgent: Agent): Agent
}