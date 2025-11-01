package org.qbrp.main.network.transport

import org.koin.core.Koin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.qbrp.main.core.fabricmc.player.PlayerReference
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.service.PlayerList
import org.qbrp.main.engine.players.model.service.ServerPlayerList
import org.qbrp.main.network.ServerPacketTransportProgram

object PlatformDependencies : KoinComponent {
    private val serverTransportProgram: ServerPacketTransportProgram by inject()
    private val playerList: ServerPlayerList by inject()

    fun fetchTransportProgram(): ServerPacketTransportProgram {
        if (getPlatform() != NetworkPlatform.SERVER) error("Используется ошибочная платформа")
        return serverTransportProgram
    }

    fun fetchPlayerList(): ServerPlayerList {
        if (getPlatform() != NetworkPlatform.SERVER) error("Используется ошибочная платформа")
        return playerList
    }
}

fun <R> Koin.fetchInPlatform(value: R?, platform: NetworkPlatform): R {
    if (getPlatform() != platform) error("Используется ошибочная платформа")
    return value ?: getOrNull() ?: error("Контекст не инициализирован")
}

class Endpoint<P : Packet>(
    val channel: NetworkChannel<P>,
) : NetworkChannel<P> by channel {
    fun sendS2C(packet: P, player: PlayerReference) {
        PlatformDependencies.fetchTransportProgram().sendS2CPacket(packet, channel, player)
    }

    fun sendAllS2C(packets: List<P>, player: PlayerReference) {
        packets.forEach { sendS2C(it, player) }
    }

    fun taskS2C(packet: P, player: PlayerReference): PacketSendTask {
        return PlatformDependencies.fetchTransportProgram().taskS2CPacket(packet, channel, player)
    }

    fun registerReceiver(handler: ServerPacketHandler<P>) {
        registerServerReceiver(channel, handler)
    }

    fun broadcast(
        packet: P,
        players: Collection<PlayerReference> = getPlayersList()
    ) {
        players.forEach {
            sendS2C(packet, it)
        }
    }

    fun broadcast(
        players: Collection<ServerPlayerObject> = getPlayersList(),
        lazyPacket: (ServerPlayerObject) -> P,
    ) {
        players.forEach {
            sendS2C(lazyPacket(it), it)
        }
    }

    private fun getPlayersList() = PlatformDependencies.fetchPlayerList().toList()
}

fun <P : Packet> NetworkChannel<P>.wrapEndpoint(): Endpoint<P> {
    return Endpoint(this)
}

inline fun <reified P : Packet> Endpoint(): Endpoint<P> {
    return NetworkChannel<P>().wrapEndpoint()
}