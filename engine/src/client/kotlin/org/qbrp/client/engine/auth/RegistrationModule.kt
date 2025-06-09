package org.qbrp.client.engine.auth

import config.ClientConfig
import net.fabricmc.api.EnvType
import org.qbrp.client.core.networking.ClientNetworkUtil
import org.qbrp.client.core.networking.ClientReceiverContext
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.QbModule
import org.qbrp.main.core.utils.networking.ClientReceiver
import org.qbrp.main.core.utils.networking.messages.Message
import org.qbrp.main.core.utils.networking.messages.Messages
import org.qbrp.main.core.utils.networking.messages.types.StringContent

@Autoload(env = EnvType.CLIENT)
class RegistrationModule(): QbModule("registration") {
    val password: String
        get() = ClientConfig.accountCode

    override fun onLoad() {
        ClientReceiver(Messages.AUTH_SUCCESS, StringContent::class) { message, context, receiver ->
            ClientAuthEvent.EVENT.invoker().onAuth(context.handler)
            true
        }.register()
        ClientReceiver(Messages.AUTH_REQUEST, StringContent::class) { message, context, receiver ->
            autoLogin()
            true
        }.register()
    }

    fun autoLogin() = login(password)

    fun login(password: String) {
        ClientNetworkUtil.sendMessage(
            Message(Messages.AUTH_REQUEST, StringContent(password))
        )
    }
}