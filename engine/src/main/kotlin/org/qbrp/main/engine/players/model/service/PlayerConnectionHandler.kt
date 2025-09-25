package org.qbrp.main.engine.players.model.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.qbrp.main.core.events.EventContext
import org.qbrp.main.core.events.emit
import org.qbrp.main.core.mc.context.MinecraftContext
import org.qbrp.main.core.mc.player.disconnect
import org.qbrp.main.core.mc.player.model.PlayerSnapshot
import org.qbrp.main.core.mc.server.thread.ServerThread
import org.qbrp.main.core.storage.TableAccess
import org.qbrp.main.core.utils.log.LoggerUtil
import org.qbrp.main.engine.players.accounts.Account
import org.qbrp.main.engine.players.auth.SessionData
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.events.PlayerDisconnectEvent
import org.qbrp.main.engine.players.model.events.PlayerInitAfterJsonLoadEvent
import org.qbrp.main.engine.players.model.events.PlayerInstantiateEvent
import org.qbrp.main.engine.players.model.exception.PlayerAlreadyOnServerException
import org.qbrp.main.engine.players.model.networking.DisconnectReason
import org.qbrp.main.engine.players.model.networking.ServerPlayerNetworkHandler
import kotlin.coroutines.resume

class PlayerConnectionHandler(
    private val table: TableAccess,
    private val factory: PlayerFactory,
    private val serializer: PlayerSerializer,
    private val storage: ServerPlayerStorage,
    private val networkHandler: ServerPlayerNetworkHandler,
    private val saver: PlayerSaver,
    private val serverThread: ServerThread,
) {
    private val logger = LoggerUtil.get("player", "connection")

    suspend fun broadcastPlayerList() {
        networkHandler.sendPlayerListToAll()
    }

    context(mc: MinecraftContext)
    suspend fun addPlayerToStorage(playerObject: ServerPlayerObject) {
        suspendCancellableCoroutine { cont ->
            runInMainThreadSafe(playerObject) {
                storage.add(playerObject)
                cont.resume(Unit)
            }
        }
    }

    context(event: EventContext, mc: MinecraftContext)
    suspend fun handleAuth(entity: PlayerSnapshot, account: Account, session: SessionData) = safe(entity) {
        if (storage.any { it.playerId == session.playerId }) throw PlayerAlreadyOnServerException(session.playerId, entity.username)

        val document = table.getById(account.playerId)
        val json = document.await()?.toJson()
        val playerRegistered = json != null

        val player =
            if (playerRegistered) {
                factory.fromJson(entity, account, serializer.fromJson(json), session)
            } else {
                factory.construct(entity, account, session)
            }

        addPlayerToStorage(player)
        broadcastPlayerList()

        PlayerInitAfterJsonLoadEvent(player).emit()

        networkHandler.sendInstantiatePacket(player)

        runInMainThreadSafe(player) {
            player.enableAll()
            PlayerInstantiateEvent(player).emit()
            saver.saveObject(player)
        }
    }

    context(event: EventContext)
    fun handleDisconnect(entity: PlayerSnapshot) {
        val player = storage.getByPlayerOrNull(entity) ?: return
        try {
            storage.remove(player.id)
            saver.saveObject(player)
            PlayerDisconnectEvent(player).emit()
            player.removeAll()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    context(mc: MinecraftContext)
    suspend fun safe(player: PlayerSnapshot, task: suspend () -> Unit) = withContext(Dispatchers.IO) {
        try {
            runBlocking { task() }
        } catch (e: Exception) {
            logger.error("Возникла ошибка при входе игрока на сервер: ${e.message}")
            e.printStackTrace()
            player.disconnect(DisconnectReason("Возникла ошибка при входе на сервер. Свяжитесь с администратором."))
        }
    }

    context(mc: MinecraftContext)
    fun runInMainThreadSafe(player: PlayerSnapshot, task: suspend () -> Unit) {
        serverThread.execute {
            runBlocking { safe(player, task) }
        }
    }
}