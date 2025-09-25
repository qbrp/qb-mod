package org.qbrp.main.engine.physics.environment

import org.qbrp.main.engine.physics.AgentEntity

interface EnvironmentProvider {
    fun getEnvironment(agent: AgentEntity): Environment
}