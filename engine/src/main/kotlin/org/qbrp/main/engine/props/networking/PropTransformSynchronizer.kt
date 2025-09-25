package org.qbrp.main.engine.props.networking

import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.networking.sender.ServerPacketSender
import org.qbrp.main.engine.items.model.ServerItemObject
import org.qbrp.main.engine.items.service.ItemStorage
import org.qbrp.main.engine.players.model.service.ServerPlayerStorage
import org.qbrp.main.engine.props.components.SpawnedProp

class PropTransformSynchronizer(
    private val itemStorage: ItemStorage<ServerItemObject>,
    private val playerStorage: ServerPlayerStorage,
    private val packetSender: ServerPacketSender
) {
    private val props get() = itemStorage.mapNotNull { it.get<SpawnedProp>() }

    fun syncProps() {
        val players = playerStorage.getAll().toList()
        for (player in players) {
            val nearestProps = props.filter { prop -> prop.pos.squaredDistanceTo(player.pos) < SYNC_DISTANCE }
            val transformsMap = nearestProps.associate { it.item.id to it.physics() }
            if (transformsMap.isEmpty()) continue
            packetSender.sendImmediately(PropTransformBatchPacket(transformsMap), CHANNEL, player)
        }
    }

    companion object {
        const val SYNC_DISTANCE = 64.0 * 64.0
        const val CHANNEL = "prop_sync"
    }
}