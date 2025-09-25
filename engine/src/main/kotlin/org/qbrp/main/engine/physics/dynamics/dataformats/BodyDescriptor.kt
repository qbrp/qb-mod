package org.qbrp.main.engine.physics.dynamics.dataformats

interface BodyDescriptor: Descriptor {
    val shape: Shape
    val transform: Transform
    val allowSleep: Boolean get() = true
}