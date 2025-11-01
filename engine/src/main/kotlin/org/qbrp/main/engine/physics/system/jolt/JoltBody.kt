package org.qbrp.main.engine.physics.system.jolt

import com.github.stephengold.joltjni.PhysicsSystem
import com.github.stephengold.joltjni.enumerate.EActivation
import com.github.stephengold.joltjni.enumerate.EMotionType
import com.github.stephengold.joltjni.Body as JoltBody
import com.github.stephengold.joltjni.Vec3 as JoltVec3
import org.qbrp.main.engine.physics.system.entity.Body
import org.qbrp.main.engine.physics.system.entity.BodyId
import org.qbrp.main.engine.physics.system.entity.PhysicsVec3
import org.qbrp.main.engine.physics.system.entity.Shape
import org.qbrp.main.engine.physics.system.entity.Transform
import org.qbrp.main.engine.physics.system.entity.asPhysical

class JoltBody(
    val numericId: Int,
    override var mass: Double,
    override val shape: Shape,
    internal val physicsSystem: PhysicsSystem,
    internal val joltBody: JoltBody,
    val isStatic: Boolean
) : Body {
    override val id: BodyId = BodyId(numericId)
    private val bodyInterface get() = physicsSystem.bodyInterface
    private var freezed = false

    override val transform: Transform
        get() {
            val pos = bodyInterface.getPosition(numericId).engine().asPhysical()
            val rot = bodyInterface.getRotation(numericId).engine().asPhysical()
            return Transform(pos, rot)
        }

    override var allowSleep: Boolean
        get() = joltBody.allowSleeping
        set(value) {
            joltBody.allowSleeping = value
        }

    override fun addForce(v: PhysicsVec3) {
        bodyInterface.addForce(numericId, v.jolt())
    }

    override val velocity: PhysicsVec3
        get() = bodyInterface.getLinearVelocity(numericId).engine().asPhysical()

    override fun freeze() {
        if (freezed || isStatic) return

        bodyInterface.setLinearAndAngularVelocity(numericId, JoltVec3(), JoltVec3())
        bodyInterface.setMotionType(numericId, EMotionType.Static, EActivation.Activate)

        freezed = true
    }

    override fun unfreeze() {
        if (!freezed) return

        bodyInterface.setMotionType(numericId, EMotionType.Dynamic, EActivation.Activate)
        freezed = false
    }
}
