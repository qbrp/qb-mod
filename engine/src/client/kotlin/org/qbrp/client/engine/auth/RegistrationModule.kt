package org.qbrp.client.engine.auth

import config.ClientConfig
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import org.koin.core.component.get
import org.koin.dsl.module

import org.qbrp.client.core.networking.v2.ClientContext
import org.qbrp.client.core.networking.v2.ClientPacketSender
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.networking.PacketListenerActivator
import org.qbrp.main.engine.players.auth.AuthModule
import org.qbrp.main.engine.players.auth.AuthPacket
import org.qbrp.main.engine.players.auth.AuthSuccessPacket
import org.qbrp.main.engine.players.auth.RegistrationListener
import org.qbrp.main.engine.players.auth.SessionData

@Autoload(env = EnvType.CLIENT)
class RegistrationModule(): EngineModule("registration"), AuthAPI, RegistrationListener {
    override val password: String
        get() = ClientConfig.accountCode
    private var sessionData: SessionData? = null

    override fun getKoinModule() = module {
        factory<SessionData> { sessionData ?: throw RuntimeException("Игрок не подключен к серверу или не авторизован") }
        single<AuthAPI> { this@RegistrationModule }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onLoad() {
        get<PacketListenerActivator<ClientContext>>().run("auth", this, { buf, ctx -> AuthSuccessPacket(buf) })
        ClientPlayConnectionEvents.JOIN.register { handler, sender, client ->
            GlobalScope.launch {
                delay(1000)
                autoLogin()
            }
        }
    }

    fun autoLogin() = login(password)

    fun login(password: String) {
        get<ClientPacketSender>().send(AuthPacket(password), AuthModule.AUTH_CHANNEL)
    }

    override fun onAuthSuccess(data: SessionData) {
        sessionData = data
        ClientAuthEvent.EVENT.invoker().onAuth(data)
    }
}