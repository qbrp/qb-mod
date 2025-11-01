package org.qbrp.main.network

import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.fabricmc.player.MinecraftPlayerId
import org.qbrp.main.core.fabricmc.player.PlayerReference
import org.qbrp.main.core.fabricmc.player.Username
import org.qbrp.main.core.fabricmc.player.asReference
import org.qbrp.main.network.player.ConnectionSession
import org.qbrp.main.network.player.DisconnectReason
import org.qbrp.main.network.player.PlayerConnectHandler
import java.util.UUID

class Timeouts(var authorized: Boolean, var antiCheatPassed: Boolean, val expires: Long, var allVerified: Boolean)

class ServerConnectionManager(
    private val server: MinecraftServer,
    private val connectHandler: PlayerConnectHandler
) {
    private val sessions: MutableMap<UUID, ConnectionSession> = mutableMapOf()
    private val timeouts: MutableMap<ConnectionSession, Timeouts> = mutableMapOf()

    fun onConnect(entity: ServerPlayerEntity) {
        val uuid = entity.uuid
        val session = ConnectionSession(
            UUID.randomUUID(),
            Username(entity.name.string),
            MinecraftPlayerId(uuid),
            entity.hasPermissionLevel(4)
        )
        sessions[uuid] = session
        timeouts[session] = Timeouts(
            authorized = false,
            antiCheatPassed = false,
            expires = System.currentTimeMillis() + EXPIRE_TIME,
            allVerified = false
        )
    }

    fun onDisconnect(entity: ServerPlayerEntity) {
        connectHandler.handleDisconnect(entity)
        val session = sessions.remove(entity.uuid)
        timeouts.remove(session)
    }

    fun getSession(handler: ServerPlayNetworkHandler): ConnectionSession {
        return sessions[handler.player.uuid]!!
    }

    fun disconnect(connectionSession: ConnectionSession, reason: DisconnectReason) = server.execute {
        val networkHandler = connectionSession.tempId.asReference().entity.networkHandler
        if (!networkHandler.isConnectionOpen) return@execute
        networkHandler.disconnect(reason.text)
    }

    fun markAntiCheatPassed(session: ConnectionSession) {
        session.timeouts.antiCheatPassed = true
        processTimeouts(session)
    }

    fun markAuthorized(session: ConnectionSession) {
        session.timeouts.authorized = true
        processTimeouts(session)
    }

    fun disconnectIfTimeoutsExpires(session: ConnectionSession): Boolean {
        val timeouts = session.timeouts
        return if (!timeouts.allVerified && System.currentTimeMillis() > timeouts.expires) {
            disconnect(session, DisconnectReason("Не отправлены пакеты данных авторизации"))
            true
        } else {
            false
        }
    }

    private fun processTimeouts(session: ConnectionSession) {
        val timeouts = session.timeouts
        if (timeouts.authorized && timeouts.antiCheatPassed) {
            timeouts.allVerified = true
        }
    }

    val PlayerReference.entity get() = server.playerManager.getPlayer(uuid.value)!!

    val ConnectionSession.timeouts get() = this@ServerConnectionManager.timeouts[this] ?: error("Неверное состояние")

    companion object {
        private const val EXPIRE_TIME = 15 * 1000
    }
}