package org.qbrp.client.engine.props

import kotlinx.serialization.Serializable
import org.qbrp.main.core.components.DataComponent
import org.qbrp.main.engine.physics.dynamics.dataformats.Transform
import org.qbrp.main.engine.physics.dynamics.dataformats.TransformAdapter
import org.qbrp.main.engine.physics.dynamics.dataformats.engine
import org.qbrp.main.engine.physics.dynamics.dataformats.joml

class PropData(private var serverTransform: TransformAdapter): DataComponent {
    private var oldTransform: TransformAdapter = serverTransform
    private var factor: Double = 0.0

    companion object {
        const val INTERP_TIME = 0.05
    }

    var transform: TransformAdapter
        set(value) {
            oldTransform = this.transform
            serverTransform = value
            factor = 0.0
        }
        get() {
            val lerpPos = oldTransform.pos.lerp(serverTransform.pos, factor)
            val lerpRot = oldTransform.quat.joml().nlerp(serverTransform.quat.joml(), factor.toFloat()).engine()
            return TransformAdapter(lerpPos, lerpRot)
        }

    fun updateFactor(dt: Double) {
        factor = (factor + dt / INTERP_TIME).coerceIn(0.0, 1.0)
    }
}