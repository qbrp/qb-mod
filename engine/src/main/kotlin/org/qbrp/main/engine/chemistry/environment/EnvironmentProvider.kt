package org.qbrp.main.engine.chemistry.environment

import org.qbrp.main.engine.chemistry.AgentEntity

interface EnvironmentProvider {
    fun getEnvironment(agent: AgentEntity): Environment
}