package org.qbrp.main.engine.physics.dynamics.dataformats

interface Body {
    val id: Int
    val transform: Transform
    val velocity: Vec3Adapter
    var allowSleep: Boolean
    var mass: Double
    fun addForce(v: Vec3Adapter)
}