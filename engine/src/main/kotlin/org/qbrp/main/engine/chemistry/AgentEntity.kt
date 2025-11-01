package org.qbrp.main.engine.chemistry

import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.components.serialization.Identifiable
import org.qbrp.main.engine.chemistry.agent.Agent

interface AgentEntity: Identifiable, ComponentManager {
    val agent: Agent? get() = get()
    val requireAgent: Agent get() = agent ?: throw NullPointerException("Агент не инициализирован")
}