package org.qbrp.core.visual.data

import net.minecraft.world.World
import org.qbrp.core.visual.VisualDataStorage
import org.qbrp.system.networking.messages.components.Cluster

abstract class VisualData(val world: World, val uuid: String, open val x: Int, open val y: Int, open val z: Int) {
    companion object { val networking = VisualDataStorage.visualDataNetworking}
    abstract fun toCluster(): Cluster
    fun broadcastHardUpdate() {
        networking.sendHardSingleUpdateMessage(this)
    }
}