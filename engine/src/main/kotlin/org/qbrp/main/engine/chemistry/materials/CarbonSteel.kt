package org.qbrp.main.engine.chemistry.materials

import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.engine.chemistry.components.Friction
import org.qbrp.main.engine.chemistry.components.Temperature
import org.qbrp.main.engine.chemistry.materials.Material
import org.qbrp.main.engine.chemistry.materials.MaterialInstantiateContext

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