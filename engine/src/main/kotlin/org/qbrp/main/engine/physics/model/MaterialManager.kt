package org.qbrp.main.engine.physics.model

import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.construct.ComponentBuilderOperator
import org.qbrp.main.core.components.state.ComponentState
import org.qbrp.main.engine.physics.model.agent.Agent
import org.qbrp.main.engine.physics.model.materials.MaterialInstantiateContext

class MaterialManager() : ComponentState<Agent>(), ComponentBuilderOperator<MaterialInstantiateContext> {
    fun materialCtx(proportion: Double, externalState: ComponentManager = parent.externalState!!)
        = MaterialInstantiateContext(this, externalState, proportion)
}