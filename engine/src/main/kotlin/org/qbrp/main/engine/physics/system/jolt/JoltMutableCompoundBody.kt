package org.qbrp.main.engine.physics.system.jolt

import com.github.stephengold.joltjni.MutableCompoundShape
import org.qbrp.main.core.util.math.asVec3
import org.qbrp.main.engine.physics.system.entity.Body
import org.qbrp.main.engine.physics.system.entity.MutableCompoundBody
import org.qbrp.main.engine.physics.system.entity.Shape
import org.qbrp.main.engine.physics.system.entity.Transform

class JoltMutableCompoundBody(
    private val body: JoltBody,
    private val joltCompoundShape: MutableCompoundShape,
) : MutableCompoundBody, Body by body {
    override fun addShape(
        shape: Shape,
        transform: Transform
    ): Int {
        return joltCompoundShape.addShape(
            transform.pos.asVec3().jolt(),
            transform.rot.jolt(),
            createJoltShape(shape)
        )
    }

    override fun removeShape(index: Int) {
        joltCompoundShape.removeShape(index)
    }
}