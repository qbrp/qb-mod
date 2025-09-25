package org.qbrp.main.mechanics.materials

import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.engine.physics.components.Friction
import org.qbrp.main.engine.physics.components.Temperature
import org.qbrp.main.engine.physics.model.materials.Material
import org.qbrp.main.engine.physics.model.materials.MaterialInstantiateContext

class CarbonSteel(ctx: MaterialInstantiateContext): Material(ctx = ctx, name = "Carbon Steel", density = 7850.0, molarMass = 55.85) {
    init {
        add(
            Temperature(
                ctx = this.ctx,
                specificHeat = 385,
                conductivity = 50.2
            )
        )
        add(Friction(
            static = 1.1,
            kinetic = 1.0
        ))
    }
}