package org.qbrp.main.network.player

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.Serializable
import net.minecraft.server.network.ServerPlayerEntity
import org.koin.core.component.KoinComponent
import org.qbrp.main.core.events.emit
import org.qbrp.main.core.fabricmc.player.MinecraftPlayerId
import org.qbrp.main.core.fabricmc.player.Username
import org.qbrp.main.core.fabricmc.server.LogicThread
import org.qbrp.main.engine.items.model.ServerItemObject
import org.qbrp.main.engine.items.service.ItemStorage
import org.qbrp.main.engine.players.accounts.Account
import org.qbrp.main.engine.players.characters.components.applyAccountCharacter
import org.qbrp.main.engine.players.characters.components.createCharactersBank
import org.qbrp.main.engine.players.model.PlayerProfile
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.PlayerDisconnectEvent
import org.qbrp.main.engine.players.model.PlayerInitAfterJsonLoadEvent
import org.qbrp.main.engine.players.model.service.PlayerFactory
import org.qbrp.main.engine.players.model.service.PlayerList
import org.qbrp.main.engine.players.model.service.PlayerService
import org.qbrp.main.engine.players.model.service.PlayersTable
import org.qbrp.main.engine.players.model.service.ServerPlayerStorage
import org.qbrp.main.engine.players.model.service.decodePlayerFromJson
import org.qbrp.main.core.util.PlayerId
import org.qbrp.main.engine.players.model.service.ServerPlayerList
import org.qbrp.main.network.inventory.synchronizeAllInventories
import org.qbrp.main.network.item.sendNearestItemsPackets
import org.qbrp.main.network.transport.NetworkChannel
import org.qbrp.main.network.transport.Packet
import org.qbrp.main.network.transport.PacketCodec
import org.qbrp.main.network.transport.wrapEndpoint
import java.util.UUID
import kotlin.coroutines.resume

// Player List

val PLAYER_LIST_ENDPOINT = NetworkChannel<PlayerListPacket>().wrapEndpoint()

@Serializable
data class PlayerListPacket(val playerListDto: PlayerListDto) : Packet

@Serializable
data class PlayerListDto(val entries: List<Entry>) {
    @Serializable
    data class Entry(val playerId: String, val username: String, val minecraftPlayerId: String)
}

fun PlayerListDto.map(): List<PlayerProfile> {
    return entries.map {
        PlayerProfile(
            PlayerId(it.playerId),
            Username(it.username),
            MinecraftPlayerId(UUID.fromString(it.minecraftPlayerId)))
    }
}

fun ServerPlayerList.toDto(): PlayerListDto {
    val entries = map { PlayerListDto.Entry(it.id, it.username.value, it.uuid.value.toString()) }
    return PlayerListDto(entries)
}

// Join Game

val JOIN_GAME_ENDPOINT = NetworkChannel<JoinGamePacket>(
    codec = PacketCodec.Binary(
        {
            JoinGamePacket(
                PlayerId(readString()),
                Username(readString())
            )
        },
        {
            writeString(it.playerId.value)
            writeString(it.username.value)
        }
    )
)
    .wrapEndpoint()

@Serializable
data class JoinGamePacket(val playerId: PlayerId, val username: Username) : Packet

class PlayerConnectHandler(
    private val table: PlayersTable,
    private val factory: PlayerFactory,
    private val storage: ServerPlayerStorage,
    private val serverThread: LogicThread,
    private val playerList: ServerPlayerList,
    private val itemStorage: ItemStorage<ServerItemObject>,
    private val playerService: PlayerService
) : KoinComponent {

    suspend fun addPlayerToStorage(playerObject: ServerPlayerObject) {
        suspendCancellableCoroutine { cont ->
            serverThread.execute {
                storage.add(playerObject)
                cont.resume(Unit)
            }
        }
    }

    suspend fun handleAuth(entity: ServerPlayerEntity, account: Account, session: ConnectionSession) {
        val username = session.username
        val playerId = PlayerId(account.playerId)
        if (storage.any { it.playerId == playerId }) throw PlayerAlreadyOnServerException(playerId, username)

        val document = table.getById(playerId.value)
        val json = document.await()?.toJson()
        val playerRegistered = json != null

        val player =
            if (playerRegistered) {
                factory.fromJson(entity, account, decodePlayerFromJson(json), session)
            } else {
                factory.construct(entity, account, session)
            }

        player.sendServerInformationPacket(getKoin())

        addPlayerToStorage(player)
        playerList.broadcast()

        val bank = player.createCharactersBank()

        PlayerInitAfterJsonLoadEvent(player).emit()

        player.sendPermissionsPacket()
        player.sendNearestItemsPackets(itemStorage.getAll().toList())
        player.sendJoinGamePacket()
        bank.applyAccountCharacter()
        player.synchronizeAllInventories()

        serverThread.execute { playerService.instantiatePlayer(player) }
    }

    fun handleDisconnect(entity: ServerPlayerEntity) {
        val player = storage.getByPlayerOrNull(entity) ?: return
        try {
            PlayerDisconnectEvent(player).emit()
            playerService.destroyPlayer(player)
        } catch (e: Exception) {
            if (storage.getById(player.id) != null) storage.remove(player.id)
            e.printStackTrace()
        }
    }
}

// Transport

suspend fun ServerPlayerObject.sendPermissionsPacket() {
    this
        .taskPermissionsPacket()
        .queue()
        .requestAcknowledge()
}

suspend fun ServerPlayerList.broadcast(): Unit = coroutineScope {
    map { player ->
        async {
            PLAYER_LIST_ENDPOINT
                .taskS2C(PlayerListPacket(toDto()), player)
                .queue()
                .requestAcknowledge()
        }
    }.awaitAll()
}

suspend fun ServerPlayerObject.sendJoinGamePacket() {
    JOIN_GAME_ENDPOINT
        .taskS2C(JoinGamePacket(playerId, username), this)
        .queue()
        .requestAcknowledge()
}

class PlayerAlreadyOnServerException(val playerId: PlayerId, val username: Username) : RuntimeException("Игрок $username ($playerId) уже на сервере")
