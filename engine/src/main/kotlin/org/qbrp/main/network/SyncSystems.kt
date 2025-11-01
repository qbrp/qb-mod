package org.qbrp.main.network

import org.qbrp.main.engine.items.model.ServerItemObject
import org.qbrp.main.engine.items.service.ItemStorage
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.service.PlayerList
import org.qbrp.main.engine.players.model.service.ServerPlayerList
import org.qbrp.main.network.item.ItemNetFrame
import org.qbrp.main.network.item.runItemSynchronizationThread
import org.qbrp.main.network.player.broadcastPermissionPackets
import org.qbrp.main.network.player.runEntitySynchronizationThread
import java.util.concurrent.ConcurrentLinkedQueue

class NetworkSystems(
    private val playerList: PlayerList,
    private val itemStorage: ItemStorage<ServerItemObject>
) {
    private var ticks = 0
    private val playerSnapshotQueue: ConcurrentLinkedQueue<ServerPlayerObject> = ConcurrentLinkedQueue()
    private val itemNetFrameQueue: ConcurrentLinkedQueue<ItemNetFrame> = ConcurrentLinkedQueue()

    fun run() {
        runEntitySynchronizationThread(playerSnapshotQueue, playerList)
        runItemSynchronizationThread(itemNetFrameQueue)
    }

    fun tickNetworkSystems(playerList: ServerPlayerList) {
        ticks++
        if (ticks % 20 == 0) {
            broadcastPermissionPackets()
        } else if (ticks % 10 == 0) {
            playerSnapshotQueue.addAll(playerList)
            itemNetFrameQueue.add(
                ItemNetFrame(
                    itemStorage.getAll().toList(),
                    playerList.toList()
                )
            )
        }
    }
}