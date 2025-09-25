package org.qbrp.main.mechanics.materials

import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.engine.physics.components.Friction
import org.qbrp.main.engine.physics.components.Temperature
import org.qbrp.main.engine.physics.model.materials.Material
import org.qbrp.main.engine.physics.model.materials.MaterialInstantiateContext

class Copper(ctx: MaterialInstantiateContext): Material(ctx = ctx, name = "Copper", density = 89600.0, molarMass = 63.55) {
    init {
        add(
            Temperature(
                ctx = this.ctx,
                specificHeat = 385,
                conductivity = 300.0
            )
        )
        add(Friction(
            static = 1.1,
            kinetic = 1.0
        ))
    }
}