package org.qbrp.main.engine.physics.system.service

import org.qbrp.main.core.fabricmc.eyePos
import org.qbrp.main.core.fabricmc.rotationVector
import org.qbrp.main.core.util.math.asVec3
import org.qbrp.main.core.util.math.convertToPhysics
import org.qbrp.main.engine.physics.system.entity.Body
import org.qbrp.main.engine.world.location.WorldVec3
import org.qbrp.main.engine.world.location.asWorldVec3

class ManipulatorSystem {
    private val pinnedBodies: MutableList<PinnedBody> = mutableListOf()

    class PinnedBody(val body: Body, val target: ManipulationTarget)

    fun onStep() {
        for (pinnedBody in pinnedBodies) {
            val body = pinnedBody.body
            val target = pinnedBody.target
            val targetPos: WorldVec3 = when (target) {
                is ManipulationTarget.Position -> target.value().asVec3()
                is ManipulationTarget.View -> {
                    val player = target.player
                    val dir = player.rotationVector.normalize()
                    val eyePos = player.eyePos
                    eyePos.asVec3().add(dir.mul(target.depth().toDouble())).asWorldVec3()
                }
            }
            val force = targetPos.sub(body.transform.pos.asVec3())
            body.addForce(
                force.mul(0.4)
                    .asWorldVec3()
                    .convertToPhysics()
            )
            /*println("Примененная сила: $force")
            println("Текущая скорость: " + body.velocity)*/
        }
    }

    fun pin(
        body: Body,
        target: ManipulationTarget
    ) {
        pinnedBodies += PinnedBody(body, target)
    }

    fun unpin(body: Body) {
        pinnedBodies.removeIf { it.body == body }
    }
}