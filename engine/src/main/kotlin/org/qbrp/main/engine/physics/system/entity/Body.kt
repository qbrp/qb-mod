package org.qbrp.main.engine.physics.system.entity

interface Body {
    val id: BodyId
    val transform: Transform
    val shape: Shape
    val velocity: PhysicsVec3
    var allowSleep: Boolean
    var mass: Double
    fun addForce(v: PhysicsVec3)
    fun freeze()
    fun unfreeze()
}