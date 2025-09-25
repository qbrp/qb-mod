package org.qbrp.main.engine.physics.model

import org.qbrp.main.engine.physics.dynamics.dataformats.Shape

interface VolumetricShape: Shape {
    val volume: Double
}