package org.qbrp.main.engine.items.networking

import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.networking.sender.ServerPacketSender
import org.qbrp.main.core.networking.getPlayersInRadius
import org.qbrp.main.core.networking.serialization.impl.GlobalComponentPacketDtoMapper
import org.qbrp.main.core.networking.serialization.mapping.SerializerKeyRegistry
import org.qbrp.main.core.networking.synchronization.SynchronizerComponent
import org.qbrp.main.engine.items.model.ItemObject
import org.qbrp.main.engine.items.components.ItemComponentInstantiateContext
import org.qbrp.main.engine.items.components.meta.ItemLocation
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.service.ServerPlayerStorage

class ItemSynchronizer(
    ctx: ItemComponentInstantiateContext,
    packetSender: ServerPacketSender,
    mapper: GlobalComponentPacketDtoMapper,
    serializerKeyRegistry: SerializerKeyRegistry,
    playerStorage: ServerPlayerStorage,
) : SynchronizerComponent<ItemObject>(ctx.componentManager, packetSender, playerStorage) {

    override val defaultChannel: String = CHANNEL_NAME
    private val dtoSynchronizer = ItemDtoSynchronizer(ctx.componentManager, mapper, packetSender, serializerKeyRegistry, CHANNEL_NAME_DTO)

    override fun createPacket(): ItemSyncPacket = ItemSyncPacket(this@ItemSynchronizer.state.parent)

    override fun getTargetsForSync(): List<ServerPlayerObject> {
        val loc = state.get<ItemLocation>() ?: return emptyList()
        return getPlayersInRadius(loc.pos, loc.world, SYNC_DISTANCE) +
                state.filterIsInstance<PlayerToSync>().flatMap { it.providePlayersToSync() }
    }

    override fun onTimer(players: List<ServerPlayerObject>) {
        super.onTimer(players)
        dtoSynchronizer.syncDtos(players)
    }

    override fun sendGlobal() {
        super.sendGlobal()
        dtoSynchronizer.syncDtos(playerStorage.getAll().toList())
    }

    fun onPlayerInitialize(player: ServerPlayerObject) {
        if (player in getTargetsForSync()) {
            sendBatch(player)
        }
        if (globalSynchronization) sendGlobal()
    }

    companion object {
        const val CHANNEL_NAME = "item-sync"
        const val CHANNEL_NAME_DTO = "item-sync-dto"
        const val SYNC_DISTANCE = 32.0
    }
}
