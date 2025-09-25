package org.qbrp.main.engine.physics.dynamics.dataformats

import org.qbrp.main.engine.physics.dynamics.CollisionLayers

interface PlaneDescriptor: Descriptor, LayerDescriptor {
    val y: Int
    override val layer: Int
        get() = CollisionLayers.WORLD
}