package org.qbrp.main.network.player

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.qbrp.main.core.fabricmc.player.MinecraftPlayerId
import org.qbrp.main.core.fabricmc.player.Username
import org.qbrp.main.core.fabricmc.server.LogicThread
import org.qbrp.main.engine.players.accounts.Account
import org.qbrp.main.engine.players.accounts.AccountDatabaseService
import org.qbrp.main.network.player.AuthorizationPacket.Success
import org.qbrp.main.network.player.AuthorizationPacket.Try
import org.qbrp.main.network.transport.NetworkChannel
import org.qbrp.main.network.transport.Packet
import org.qbrp.main.network.transport.PacketCodec
import org.qbrp.main.network.transport.ServerNetworkContext
import org.qbrp.main.network.transport.wrapEndpoint

fun KoinComponent.runServerAuthorizationListeners(handler: AuthorizationHandler) {
    AUTHORIZE_TRY_ENDPOINT
        .registerReceiver { ctx -> handler.onAuthRequest(password) }
}

sealed class AuthorizationPacket : Packet {
    data class Try(val password: String) : AuthorizationPacket()
    data class Success(val session: ConnectionSession) : AuthorizationPacket()
}

val AUTHORIZE_TRY_ENDPOINT = NetworkChannel<Try>(
    "try-auth",
    PacketCodec.Binary(
        { Try(readString()) },
        { writeString(it.password) }
    )
).wrapEndpoint()

val AUTHORIZE_SUCCESS_ENDPOINT = NetworkChannel<Success>(
    "success-auth",
    PacketCodec.Binary(
        {
            Success(
                ConnectionSession(
                    readUuid(),
                    Username(readString()),
                    MinecraftPlayerId(readUuid()),
                    readBoolean()
                )
            )
        },
        {
            writeUuid(it.session.token)
            writeString(it.session.username.value)
            writeUuid(it.session.tempId.value)
            writeBoolean(it.session.isOp)
        }
    )
).wrapEndpoint()

class AuthorizationHandler(
    private val accountDatabaseService: AccountDatabaseService,
    private val connectHandler: PlayerConnectHandler,
    private val mainThread: LogicThread
) {
    private val coroutineScope by lazy { CoroutineScope(Dispatchers.IO + SupervisorJob()) }

    context(ctx: ServerNetworkContext)
    fun onAuthRequest(password: String) {
        coroutineScope.launch { handleAuth(password) }
    }

    context(ctx: ServerNetworkContext)
    private suspend fun handleAuth(auth: String) {
        val connection = ctx.connectionSession
        val result = login(connection.username.value, auth)

        when (result) {
            is LoginResult.AlreadyLinked -> connection.disconnect(DisconnectReason("Имя пользователя уже привязано к другому аккаунту."))

            is LoginResult.NotFound -> connection.disconnect(DisconnectReason("Аккаунт не найден."))

            is LoginResult.Error -> connection.disconnect(DisconnectReason("Возникла ошибка при входе в аккаунт: ${result.ex}."))

            is LoginResult.Success -> {
                handleLogin(result.account)
            }
        }
    }

    private suspend fun login(playerName: String, uuid: String): LoginResult = withContext(Dispatchers.IO) {
        try {
            accountDatabaseService.get(uuid)?.let {
                if (!accountDatabaseService.isNicknameAlreadyRegistered(playerName)) {
                    it.updateRegisteredNicknames(playerName)
                    LoginResult.Success(it)
                } else {
                    LoginResult.AlreadyLinked()
                }
            } ?: LoginResult.NotFound()
        } catch (e: Exception) {
            LoginResult.Error(e)
        }
    }

    context(ctx: ServerNetworkContext)
    private suspend fun handleLogin(account: Account) {
        val connection = ctx.connectionSession
        try {
            AUTHORIZE_SUCCESS_ENDPOINT
                .taskS2C(Success(connection), connection)
                .queue()
                .requestAcknowledge()
            ctx.serverDependencies.connectionManager.markAuthorized(ctx.connectionSession)
            connectHandler.handleAuth(ctx.playerEntity, account, connection)
        } catch (e: Exception) {
            connection.disconnect(DisconnectReason("Возникла ошибка при обработке: ${e.message}"))
            e.printStackTrace()
        }
    }

    private fun onMainThread(r: Runnable) = mainThread.execute(r)
}