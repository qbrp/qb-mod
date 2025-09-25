package org.qbrp.main.engine.physics.dynamics.tracking

import net.minecraft.util.math.BlockPos
import org.qbrp.main.engine.physics.dynamics.dataformats.Vec3Adapter
import org.qbrp.main.engine.physics.dynamics.simulation.WorldBodyManager

class PlaceBreakBlockPropActivator(private val bodyManager: WorldBodyManager, var activateDistance: Double = 12.0) {
    fun onBlockUpdated(pos: BlockPos) {
        bodyManager.apply {
            nearest(Vec3Adapter(pos.toCenterPos()), activateDistance).forEach { activate(it) }
        }
    }
}