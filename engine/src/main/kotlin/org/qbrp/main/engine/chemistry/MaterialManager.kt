package org.qbrp.main.engine.chemistry

import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.state.ComponentState
import org.qbrp.main.engine.chemistry.agent.Agent
import org.qbrp.main.engine.chemistry.materials.MaterialInstantiateContext

class MaterialManager() : ComponentState<Agent>() {
    fun materialCtx(proportion: Double, externalState: ComponentManager = parent.externalState!!)
        = MaterialInstantiateContext(this, externalState, proportion)
}