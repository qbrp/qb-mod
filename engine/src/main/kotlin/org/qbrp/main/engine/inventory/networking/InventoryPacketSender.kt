package org.qbrp.main.engine.inventory.networking

import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.inventory.entity.Inventory
import org.qbrp.main.engine.inventory.entity.InventoryGroup
import org.qbrp.main.engine.players.model.ServerPlayerObject

interface InventoryPacketSender {
    fun sendInventoryGroupPacket(player: ServerPlayerObject, group: InventoryGroup)
    fun sendInventoryClosePacket(player: ServerPlayerObject, id: ContainerId)
}