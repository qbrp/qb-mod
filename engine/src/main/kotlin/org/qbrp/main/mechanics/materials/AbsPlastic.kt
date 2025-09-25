package org.qbrp.main.mechanics.materials

import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.engine.physics.components.Friction
import org.qbrp.main.engine.physics.components.Temperature
import org.qbrp.main.engine.physics.model.materials.Material
import org.qbrp.main.engine.physics.model.materials.MaterialInstantiateContext

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