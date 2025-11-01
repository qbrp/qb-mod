package org.qbrp.main.engine.physics.system.jolt

import com.github.stephengold.joltjni.Character
import com.github.stephengold.joltjni.enumerate.EActivation
import org.qbrp.main.core.util.math.Pos
import org.qbrp.main.core.util.math.asVec3
import org.qbrp.main.engine.physics.system.entity.PhysicalCharacter
import org.qbrp.main.engine.physics.system.entity.PhysicsVec3
import org.qbrp.main.engine.physics.system.entity.Transform
import org.qbrp.main.engine.physics.system.entity.asPhysical

class JoltCharacter(
    val jolt: Character,
) : PhysicalCharacter {
    override val transform: Transform
        get() = jolt.worldTransform.let {
            Transform(
                it.translation.engine().asPhysical(),
                it.quaternion.engine().asPhysical()
            )
        }

    override var linearVelocity: PhysicsVec3
        get() = jolt.linearVelocity.engine().asPhysical()
        set(value) {
            jolt.setLinearVelocity(value.jolt())
        }

    override val isSupported: Boolean
        get() = jolt.isSupported

    override fun setPos(pos: Pos) {
        jolt.setPosition(pos.asVec3().joltR(), EActivation.Activate)
    }

    override fun addImpulse(vec: PhysicsVec3) {
        jolt.addImpulse(vec.jolt())
    }
}