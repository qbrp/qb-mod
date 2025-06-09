package org.qbrp.main.core.synchronization.position

import net.minecraft.util.math.Vec3d
import org.qbrp.main.core.mc.player.ServerPlayerObject
import org.qbrp.main.core.synchronization.state.ObjectSynchronizable

interface SquaredRadiusSynchronizable: ObjectSynchronizable {
    val pos: Vec3d
    val syncDistance: Int
    override fun shouldSync(player: ServerPlayerObject): Boolean {
        return player.entity.squaredDistanceTo(pos) <= syncDistance
    }
}