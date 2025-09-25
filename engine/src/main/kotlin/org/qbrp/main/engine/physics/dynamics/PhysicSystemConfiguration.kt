package org.qbrp.main.engine.physics.dynamics

interface PhysicSystemConfiguration {
    val maxBodies: Int
    val numBodyMutexes: Int
    val maxBodyPairs: Int
    val maxContacts: Int
}