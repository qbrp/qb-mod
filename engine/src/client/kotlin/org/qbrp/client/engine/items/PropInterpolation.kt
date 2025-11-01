package org.qbrp.client.engine.items

import org.qbrp.main.core.components.DataComponent
import org.qbrp.main.core.components.extensions.getOrAdd
import org.qbrp.main.core.components.extensions.has
import org.qbrp.main.core.components.extensions.remove
import org.qbrp.main.core.util.math.asVec3
import org.qbrp.main.engine.physics.system.entity.Body
import org.qbrp.main.engine.physics.system.entity.Transform
import org.qbrp.main.engine.physics.system.entity.asPhysical
import org.qbrp.main.engine.physics.system.jolt.engine
import org.qbrp.main.engine.physics.system.jolt.joml

class PropInterpolation(private val body: Body) : DataComponent {
    private val actualTransform get() = body.transform
    private var lastTransform: Transform = actualTransform

    fun getTransform(dt: Double): Transform {
        val lerpPos = lastTransform.pos.asVec3().lerp(actualTransform.pos.asVec3(), dt)
        val lerpRot = lastTransform.rot.joml()
            .nlerp(actualTransform.rot.joml(), dt.toFloat())
            .engine()

        lastTransform = Transform(lerpPos.asPhysical(), lerpRot.asPhysical())

        return lastTransform
    }

}

fun ClientItemObject.setPropData(body: Body) {
    getOrAdd { PropInterpolation(body) }
}


fun ClientItemObject.removePropData() {
    remove<PropInterpolation>()
}

fun ClientItemObject.hasPropData() = has<PropInterpolation>()