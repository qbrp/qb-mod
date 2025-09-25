package org.qbrp.main.engine.physics.dynamics.manipulate

import com.github.stephengold.joltjni.Vec3
import net.minecraft.util.math.Vec3d
import org.qbrp.main.core.mc.player.getEntity
import org.qbrp.main.core.mc.player.service.snapshot.MinecraftPlayerSnapshotService
import org.qbrp.main.engine.physics.dynamics.dataformats.Body
import org.qbrp.main.engine.physics.dynamics.dataformats.Vec3Adapter

class ManipulatorImpl(private val snapshotService: MinecraftPlayerSnapshotService) : Manipulator {
    private val pinnedBodies: MutableList<PinnedBody> = mutableListOf()

    class PinnedBody(val body: Body, val target: ManipulationTarget)

    fun onStep() {
        for (pinnedBody in pinnedBodies) {
            val body = pinnedBody.body
            val target = pinnedBody.target
            val targetPos = when (target) {
                is ManipulationTarget.Position -> target.value()
                is ManipulationTarget.View -> {
                    val entity = snapshotService.snapshotEntity(target.player.uuid)
                    val dir = entity.rotationVector.normalize()
                    entity.eyePos.add(dir.mul(target.depth().toDouble()))
                }
            }
            val force = targetPos.sub(body.transform.pos)
            body.addForce(Vec3Adapter(force).mul(0.4))
            println("Примененная сила: $force")
            println("Текущая скорость: " + body.velocity)
        }
    }

    override fun pin(
        body: Body,
        target: ManipulationTarget
    ) {
        pinnedBodies += PinnedBody(body, target)
    }

    override fun unpin(body: Body) {
        pinnedBodies.removeIf { it.body == body }
    }
}