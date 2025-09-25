package org.qbrp.main.engine.physics.model.agent

import org.qbrp.main.engine.physics.model.VolumetricShape

interface Solid: Agent {
    val shape: VolumetricShape
    override val volume: Double get() = shape.volume
    //fun asProp(): PhysicalEntry
}