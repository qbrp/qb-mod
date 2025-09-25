package org.qbrp.main.engine.physics.dynamics.simulation

import com.github.stephengold.joltjni.Body as JoltBody
import org.qbrp.main.engine.physics.dynamics.dataformats.Body
import org.qbrp.main.engine.world.location.Position
import org.qbrp.main.engine.physics.dynamics.dataformats.Quat
import org.qbrp.main.engine.physics.dynamics.dataformats.Shape
import org.qbrp.main.engine.physics.dynamics.dataformats.Transform
import org.qbrp.main.engine.physics.dynamics.dataformats.Vec3Adapter
import org.qbrp.main.engine.physics.dynamics.dataformats.engine
import org.qbrp.main.engine.physics.dynamics.dataformats.jolt
import org.qbrp.main.engine.physics.dynamics.dataformats.joltR

class LoadedJoltBody(
    override val id: Int,
    override var mass: Double,
    val joltBody: JoltBody,
) : Body {
    override val transform: Transform = JoltTransform()
    override var allowSleep
        get() = joltBody.allowSleeping
        set(value) { joltBody.allowSleeping = value }

    override fun addForce(v: Vec3Adapter) {
        joltBody.addForce(v.jolt())
    }

    override val velocity: Vec3Adapter
        get() = joltBody.linearVelocity.engine()

    inner class JoltTransform(): Transform {
        override val pos: Position
            get() = joltBody.position.engine()
        override val quat: Quat
            get() = joltBody.rotation.engine()
    }
}