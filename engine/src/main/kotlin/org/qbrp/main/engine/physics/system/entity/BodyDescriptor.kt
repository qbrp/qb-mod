package org.qbrp.main.engine.physics.system.entity

import org.qbrp.main.engine.physics.system.CollisionLayers

data class BodyDescriptor(
    val shape: Shape,
    val transform: Transform,
    val mass: Double = 0.0,
    val layer: Int = CollisionLayers.PROP_DYNAMIC,
    val allowSleep: Boolean = false
)

fun RigidBody(
    shape: Shape,
    transform: Transform,
    mass: Double,
): BodyDescriptor {
    require(mass > 0.0)
    return BodyDescriptor(
        shape,
        transform,
        mass
    )
}