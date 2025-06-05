package org.qbrp.main.engine.synchronization.position

import net.minecraft.util.math.Vec3d
import org.qbrp.main.core.mc.player.PlayerObject
import org.qbrp.main.engine.synchronization.state.ObjectSynchronizable

interface SquaredRadiusSynchronizable: ObjectSynchronizable {
    val pos: Vec3d
    val syncDistance: Int
    override fun shouldSync(player: PlayerObject): Boolean {
        return player.entity.squaredDistanceTo(pos) >= syncDistance
    }
}