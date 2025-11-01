package org.qbrp.main.engine.chemistry.agent

import org.qbrp.main.engine.physics.system.entity.Shape

interface Solid : Agent {
    val shape: Shape
    override val volume: Double
}