package org.qbrp.engine.client.core

import org.qbrp.engine.client.core.resources.ClientResources
import org.qbrp.engine.client.system.networking.ClientNetworkManager
import org.qbrp.system.networking.messages.Message
import org.qbrp.system.networking.messages.Messages
import org.qbrp.system.networking.messages.types.StringContent

class RegistrationManager {
    fun autoLogin() {
        val code = ClientResources.root.getAutoLoginCode()
        login(code)
    }

    fun login(code: String) {
        ClientNetworkManager.sendMessage(
            Message(Messages.AUTH, StringContent(code))
        )
        //MinecraftClient.getInstance().player?.networkHandler?.sendCommand("login $code")
    }
}