package org.qbrp.main.engine.physics.system

import org.qbrp.main.core.util.math.Vec3

interface PhysicSystemConfiguration {
    val maxBodies: Int
    val numBodyMutexes: Int
    val maxBodyPairs: Int
    val maxContacts: Int
}

object BuiltinPhysicsConfiguration : PhysicSystemConfiguration {
    override val maxBodies: Int = 5000
    override val numBodyMutexes: Int = 0
    override val maxBodyPairs: Int = 65_536
    override val maxContacts: Int = 20_480
}

val BUILTIN_GRAVITY = Vec3(0.0, -10.0, 0.0)