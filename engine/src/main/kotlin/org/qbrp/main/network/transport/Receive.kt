package org.qbrp.main.network.transport

import kotlinx.serialization.InternalSerializationApi
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import org.koin.core.Koin
import org.koin.core.context.GlobalContext
import org.qbrp.main.core.fabricmc.player.PlayerReference
import org.qbrp.main.core.fabricmc.player.asReference
import org.qbrp.main.network.player.DisconnectReason
import org.qbrp.main.engine.players.model.service.ServerPlayerStorage
import org.qbrp.main.network.ServerConnectionManager
import org.qbrp.main.network.ServerPacketTransportProgram
import org.qbrp.main.network.player.ConnectionSession

interface Packet

// Context

interface NetworkContext

data class ServerDependencies(
    val playerStorage: ServerPlayerStorage,
    val server: MinecraftServer,
    val connectionManager: ServerConnectionManager,
    val koin: Koin
) {
    // ServerDependencies создается раньше ServerPacketTransportProgram, так что лучше будет инжектить так
    val transportProgram: ServerPacketTransportProgram by koin.inject()
}

data class ServerNetworkContext(
    val serverDependencies: ServerDependencies,
    val connectionSession: ConnectionSession
) : NetworkContext {
    val playerReference: PlayerReference
        get() = connectionSession.tempId.asReference()
    val player
        get() = serverDependencies.playerStorage.getByMinecraftId(playerReference.uuid)
    val username
        get() = connectionSession.username
    val playerEntity
        get() = serverDependencies.server.playerManager.getPlayer(connectionSession.tempId.value)!!
}

// Registration

typealias PacketHandler<C, P> = context(C) P.(C) -> Unit

typealias ServerPacketHandler<P> = PacketHandler<ServerNetworkContext, P>

fun reason(e: Throwable) = DisconnectReason("Ошибка обработки пакета: ${e.message}")

fun <P : Packet> registerServerReceiver(channel: NetworkChannel<P>, handler: ServerPacketHandler<P>) {
    GlobalContext.get().get<ServerPacketTransportProgram>()
        .registerServerReceiver(channel, handler)
}

@OptIn(InternalSerializationApi::class)
inline fun <reified P : Packet> onPacket(
    noinline handler: ServerPacketHandler<P>
) {
    registerServerReceiver(
        NetworkChannel<P>(),
        handler
    )
}