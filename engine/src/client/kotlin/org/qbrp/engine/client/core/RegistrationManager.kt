package org.qbrp.engine.client.core

import net.minecraft.client.MinecraftClient
import org.qbrp.engine.chat.ChatModule.Companion.SYSTEM_MESSAGE_AUTHOR
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.engine.client.EngineClient
import org.qbrp.engine.client.core.resources.ClientResources

class RegistrationManager {

    fun autoLogin() {
        val code = ClientResources.root.getAutoLoginCode()
        login(code)
    }

    fun login(code: String) {
        MinecraftClient.getInstance().player?.networkHandler?.sendCommand("login $code")
    }
}