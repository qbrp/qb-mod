package org.qbrp.main.network.player

import kotlinx.serialization.Serializable
import org.qbrp.main.core.fabricmc.player.PlayerReference
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.service.PlayerList
import org.qbrp.main.network.transport.NetworkChannel
import org.qbrp.main.network.transport.Packet
import org.qbrp.main.network.transport.PacketCodec
import org.qbrp.main.network.transport.wrapEndpoint
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.concurrent.thread

val PLAYER_ENTITY_ENDPOINT = NetworkChannel<PlayerEntityPacket>().wrapEndpoint()

@Serializable
data class PlayerEntityPacket(val entityName: String, val id: String) : Packet

val COMMAND_EXECUTE_ENDPOINT = NetworkChannel<CommandExecutePacket>(
    codec = PacketCodec.Binary(
        { CommandExecutePacket(readString()) },
        { writeString(it.command) }
    )
).wrapEndpoint()

data class CommandExecutePacket(val command: String) : Packet

fun PlayerReference.sendCommandExecutePacket(command: String) {
    COMMAND_EXECUTE_ENDPOINT
        .sendS2C(CommandExecutePacket(command), this)
}

fun runEntitySynchronizationThread(
    snapshotQueue: ConcurrentLinkedQueue<ServerPlayerObject>,
    playerList: PlayerList
) {
    thread(name = "Engine Entity Synchronization", isDaemon = true) {
        while (true) {
            while (snapshotQueue.isNotEmpty()) {
                val player = snapshotQueue.poll()
                val packets = playerList
                    .toList()
                    .filter { it != player }
                    .map { PlayerEntityPacket(it.username.value, it.playerId.value) }

                PLAYER_ENTITY_ENDPOINT
                    .sendAllS2C(packets, player)
            }
            Thread.sleep(1)
        }
    }
}