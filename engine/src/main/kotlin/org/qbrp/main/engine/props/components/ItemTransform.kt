package org.qbrp.main.engine.props.components

import org.qbrp.main.engine.physics.system.entity.PhysicsRotation
import org.qbrp.main.engine.world.location.Location

interface ItemTransform : Location {
    val quat: PhysicsRotation
    // Я так и не понял, нахуя оно здесь, если это проецирование
    // fun physics(): Transform = Transform(pos.asVec3().convertToPhysics(), quat)
}