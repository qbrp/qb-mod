package org.qbrp.main.engine.physics.system.entity

import org.qbrp.main.core.util.math.Pos

interface PhysicalCharacter {
    var linearVelocity: PhysicsVec3
    val transform: Transform
    val isSupported: Boolean
    fun setPos(pos: Pos)
    fun addImpulse(vec: PhysicsVec3)
}