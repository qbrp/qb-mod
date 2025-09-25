package org.qbrp.main.engine.players.auth

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.qbrp.main.core.events.emit
import org.qbrp.main.core.events.withEventContext
import org.qbrp.main.core.mc.context.MinecraftContext
import org.qbrp.main.core.mc.context.suspendMinecraftContext
import org.qbrp.main.core.mc.player.model.PlayerSnapshot
import org.qbrp.main.core.mc.player.disconnect
import org.qbrp.main.core.mc.server.thread.ServerThread
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.networking.PacketListenerActivator
import org.qbrp.main.core.networking.ServerNetworkContext
import org.qbrp.main.core.networking.sender.ServerPacketSender
import org.qbrp.main.engine.players.accounts.Account
import org.qbrp.main.engine.players.accounts.AccountDatabaseService
import org.qbrp.main.engine.players.auth.event.SuccessLoginEvent
import org.qbrp.main.engine.players.auth.event.TryLoginEvent
import org.qbrp.main.engine.players.model.networking.DisconnectReason
import org.qbrp.main.engine.players.model.util.PlayerId
import java.util.UUID
import kotlin.coroutines.CoroutineContext

@Autoload(Priorities.PLAYERS + 1)
class AuthModule : EngineModule("auth"), AuthListener {
    companion object {
        const val AUTH_CHANNEL = "auth"
    }

    override fun getKoinModule(): Module = innerWithApi({
    }, {
        scoped<CoroutineScope> { AuthCoroutineScope() }
    })
    private val accounts by inject<AccountDatabaseService>()
    private val serverThread: ServerThread by inject()
    private val coroutineScope get() = getLocal<CoroutineScope>()
    private val packetSender by inject<ServerPacketSender>()

    class AuthCoroutineScope(override val coroutineContext: CoroutineContext = Dispatchers.IO) : CoroutineScope

    override fun onEnable() {
        get<PacketListenerActivator<ServerNetworkContext>>().run(AUTH_CHANNEL, this, { buf, ctx -> AuthPacket(buf, ctx.player) })
    }

    override fun onAuthRequest(player: PlayerSnapshot, password: String) {
        coroutineScope.launch { handleAuth(player, password) }
    }

    private suspend fun login(playerName: String, uuid: String): LoginResult = withContext(Dispatchers.IO) {
        try {
            accounts.get(uuid)?.let {
                if (!accounts.isNicknameAlreadyRegistered(playerName)) {
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

    suspend fun handleAuth(player: PlayerSnapshot, auth: String) = suspendMinecraftContext {
        val result = login(player.username.value, auth)
        withEventContext { TryLoginEvent(player, result).emit() }

        when (result) {
            is LoginResult.AlreadyLinked -> onMainThread {
                player.disconnect(DisconnectReason("Имя пользователя уже привязано к другому аккаунту."))
            }
            is LoginResult.NotFound -> onMainThread {
                player.disconnect(DisconnectReason("Аккаунт не найден."))
            }
            is LoginResult.Error -> onMainThread {
                player.disconnect(DisconnectReason("<red>[ENGINE] Возникла ошибка при входе в аккаунт: ${result.ex}."))
            }
            is LoginResult.Success -> {
                handleLogin(player, result.account)
            }
        }
    }

    context(mc: MinecraftContext)
    private suspend fun handleLogin(player: PlayerSnapshot, account: Account) {
        val session = getSessionData(player, account)
        packetSender.send(AuthSuccessPacket(session), AUTH_CHANNEL, player)
            .queue()
            .waitAcknowledge()
        onMainThread {
            try {
                withEventContext { SuccessLoginEvent(player, account, session).emit() }
            } catch (e: Exception) {
                player.disconnect(DisconnectReason("Возникла ошибка при обработке: ${e.message}"))
                e.printStackTrace()
            }
        }
    }

    private fun getSessionData(player: PlayerSnapshot, account: Account): SessionData {
        return SessionData(UUID.randomUUID(), PlayerId(account.playerId))
    }

    private fun onMainThread(r: Runnable) = serverThread.execute(r)
}