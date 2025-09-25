package org.qbrp.main.engine.physics.environment

import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.engine.physics.AgentEntity

class ComponentEnvironmentProvider(private val world: WorldEnv): EnvironmentProvider {
    override fun getEnvironment(entity: AgentEntity): Environment {
        val envComponent = entity.get<ItemEnvironment>()?.env
        return envComponent ?: world
    }
}