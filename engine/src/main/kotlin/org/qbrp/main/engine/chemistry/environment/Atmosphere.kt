package org.qbrp.main.engine.chemistry.environment

import org.qbrp.main.engine.chemistry.agent.Agent

interface Atmosphere {
    fun getAir(forAgent: Agent): Agent
}