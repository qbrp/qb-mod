package org.qbrp.main.engine.players.model.networking

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.qbrp.main.core.mc.context.MinecraftContext
import org.qbrp.main.core.mc.player.getPermissions
import org.qbrp.main.core.mc.player.hasPermission
import org.qbrp.main.core.networking.sender.ServerPacketSender
import org.qbrp.main.engine.players.model.PlayersModule.Companion.INSTANTIATE_CHANNEL
import org.qbrp.main.engine.players.model.PlayersModule.Companion.PLAYER_LIST_CHANNEL
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.mapping.PlayerListMapper
import org.qbrp.main.engine.players.model.networking.packet.PlayerInstantiatePacket
import org.qbrp.main.engine.players.model.service.PlayerList

class ServerPlayerNetworkHandler(
    private val playerList: PlayerList,
    private val playerListMapper: PlayerListMapper,
    private val packetSender: ServerPacketSender,
    private val minecraftContext: MinecraftContext
) {
    suspend fun sendPlayerListToAll() = coroutineScope {
        playerList.map {
            async {
                packetSender
                    .send(PlayerListPacket(playerListMapper.toDto(playerList)),
                        PLAYER_LIST_CHANNEL,
                        it)
                    .log("PlayerList")
                    .queue()
                    .waitAcknowledge()
            }
        }.awaitAll()
    }

    suspend fun sendInstantiatePacket(player: ServerPlayerObject) = with(minecraftContext) {
        packetSender.send(PlayerInstantiatePacket(player.username), INSTANTIATE_CHANNEL, player)
            .queue()
            .waitAcknowledge()
    }
}