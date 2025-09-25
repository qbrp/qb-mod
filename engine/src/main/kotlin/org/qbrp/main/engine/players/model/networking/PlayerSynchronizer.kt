package org.qbrp.main.engine.players.model.networking

import org.qbrp.main.core.networking.sender.ServerPacketSender
import org.qbrp.main.core.networking.getPlayersInRadius
import org.qbrp.main.core.networking.serialization.impl.GlobalComponentPacketDtoMapper
import org.qbrp.main.core.networking.serialization.mapping.SerializerKeyRegistry
import org.qbrp.main.core.networking.synchronization.SynchronizerComponent
import org.qbrp.main.engine.items.networking.ItemSynchronizer.Companion.CHANNEL_NAME_DTO
import org.qbrp.main.engine.items.networking.ItemSynchronizer.Companion.SYNC_DISTANCE
import org.qbrp.main.engine.players.model.PlayerObject
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.components.PlayerComponentInstantiateContext
import org.qbrp.main.engine.players.model.networking.packet.PlayerSyncPacket
import org.qbrp.main.engine.players.model.service.ServerPlayerStorage

class PlayerSynchronizer(ctx: PlayerComponentInstantiateContext,
                         packetSender: ServerPacketSender,
                         mapper: GlobalComponentPacketDtoMapper,
                         serializerKeyRegistry: SerializerKeyRegistry,
                         playerStorage: ServerPlayerStorage): SynchronizerComponent<PlayerObject>(ctx.componentManager, packetSender, playerStorage) {
    override val defaultChannel: String = CHANNEL
    private val player = ctx.player as ServerPlayerObject
    private val dtoSynchronizer = PlayerDtoSynchronizer(ctx.componentManager, mapper, packetSender, serializerKeyRegistry, CHANNEL_NAME_DTO)

    override fun createPacket(): PlayerSyncPacket = PlayerSyncPacket(player)

    override fun getTargetsForSync(): List<ServerPlayerObject> {
        return getPlayersInRadius(player, SYNC_DISTANCE).toMutableList().apply { add(player) }
    }

    override fun onTimer(players: List<ServerPlayerObject>) {
        super.onTimer(players)
        dtoSynchronizer.syncDtos(players)
    }

    override fun sendGlobal() {
        super.sendGlobal()
        dtoSynchronizer.syncDtos(playerStorage.getAll().toList())
    }

    companion object {
        const val CHANNEL = "player-sync"
    }
}