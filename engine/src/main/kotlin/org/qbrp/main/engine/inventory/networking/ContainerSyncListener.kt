package org.qbrp.main.engine.inventory.networking

import org.qbrp.main.core.networking.PacketListener
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.containers.util.GlobalContainerId
import org.qbrp.main.engine.players.model.ServerPlayerObject

interface ContainerSyncListener: PacketListener {
    fun onSyncRequest(player: ServerPlayerObject, containerId: ContainerId)
}