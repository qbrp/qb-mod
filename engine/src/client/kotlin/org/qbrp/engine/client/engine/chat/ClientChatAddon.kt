package org.qbrp.engine.client.engine.chat

import org.qbrp.engine.chat.ChatAPI
import org.qbrp.engine.Engine
import org.qbrp.engine.client.EngineClient
import org.qbrp.system.modules.QbModule

abstract class ClientChatAddon(name: String) : QbModule("chat-addon-${name}") {
    init {
        dependsOn { EngineClient.isApiAvailable<ClientChatAPI>() }
    }
}