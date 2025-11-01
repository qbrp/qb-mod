package org.qbrp.main.engine.chemistry.materials

import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.engine.chemistry.components.Friction
import org.qbrp.main.engine.chemistry.components.Temperature
import org.qbrp.main.engine.chemistry.materials.Material
import org.qbrp.main.engine.chemistry.materials.MaterialInstantiateContext

class AbsPlastic(ctx: MaterialInstantiateContext): Material(ctx = ctx, name = "ABS Plastic", density = 104.0, molarMass = 100.0) {
    init {
        add(
            Temperature(
                ctx = this.ctx,
                specificHeat = 1300,
                conductivity = 0.2
            )
        )
        add(Friction(
            static = 0.5,
            kinetic = 0.4
        ))
    }
}