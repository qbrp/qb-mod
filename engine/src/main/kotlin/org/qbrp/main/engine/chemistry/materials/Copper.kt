package org.qbrp.main.engine.chemistry.materials

import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.engine.chemistry.components.Friction
import org.qbrp.main.engine.chemistry.components.Temperature
import org.qbrp.main.engine.chemistry.materials.Material
import org.qbrp.main.engine.chemistry.materials.MaterialInstantiateContext

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