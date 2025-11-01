package org.qbrp.main.engine.chemistry.materials

import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.InstantiationContext

class MaterialInstantiateContext(override val componentManager: AgentSubstance,
                                 val externalState: ComponentManager?,
                                 val proportion: Double) : InstantiationContext<AgentSubstance> {}