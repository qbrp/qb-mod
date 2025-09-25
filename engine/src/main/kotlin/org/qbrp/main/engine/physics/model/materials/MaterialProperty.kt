package org.qbrp.main.engine.physics.model.materials

import org.qbrp.main.core.components.Component
import org.qbrp.main.core.components.InstantiationContext
import org.qbrp.main.engine.physics.model.agent.Agent

typealias MaterialPropertyContext = InstantiationContext<Material>

open class MaterialProperty(ctx: MaterialPropertyContext,
                            override var state: Material = ctx.componentManager
): Component<Material> {
    protected val material get() = state
    protected val agent get() = material.agent
    protected val agentMaterials get() = agent.materialManager
    protected val external get() = material.externalState
}