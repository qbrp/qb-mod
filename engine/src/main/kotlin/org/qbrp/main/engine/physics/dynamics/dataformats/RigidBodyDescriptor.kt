package org.qbrp.main.engine.physics.dynamics.dataformats

/**
 * Дескриптор твердых тел. Масса и инерция рассчитываются на основе массы и формы.
 * **/
interface RigidBodyDescriptor: BodyDescriptor {
    override val shape: Shape
    override val mass: Double
}