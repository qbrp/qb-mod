package org.qbrp.main.engine.chemistry.materials

import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.engine.chemistry.components.Friction
import org.qbrp.main.engine.chemistry.components.Temperature
import org.qbrp.main.engine.chemistry.materials.Material
import org.qbrp.main.engine.chemistry.materials.MaterialInstantiateContext

class Glass(ctx: MaterialInstantiateContext): Material(ctx = ctx, name = "Glass", density = 2500.0, molarMass = 60.0) {
    init {
        add(
            Temperature(
                ctx = this.ctx,
                specificHeat = 800,
                conductivity = 0.8
            )
        )
        add(Friction(
            static = 0.9,
            kinetic = 0.5
        ))
    }
}