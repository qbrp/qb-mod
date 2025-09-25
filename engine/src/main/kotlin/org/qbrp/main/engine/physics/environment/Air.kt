package org.qbrp.main.engine.physics.environment

import org.qbrp.main.core.components.construct.ComponentBuilderOperator
import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.engine.physics.model.agent.Gas
import org.qbrp.main.engine.physics.model.MaterialManager
import org.qbrp.main.engine.physics.model.materials.Material
import org.qbrp.main.engine.physics.model.materials.MaterialInstantiateContext

class Air(
    override var temperature: Double,
    override val volume: Double,
    override val displayName: String = "Воздух",
    override val materialManager: MaterialManager = MaterialManager(),
) : Gas, ComponentBuilderOperator<MaterialInstantiateContext> by materialManager {
    init {
        add(Material(ctx = materialCtx(0.78), name = "Nitrogen", density = 1.2506, molarMass = 28.02))
        add(Material(ctx = materialCtx(0.21), name = "Oxygen",  density = 1.4290, molarMass = 32.00))
        add(Material(ctx = materialCtx(0.01), name = "Argon", density = 1.784, molarMass = 39.95))
    }
}