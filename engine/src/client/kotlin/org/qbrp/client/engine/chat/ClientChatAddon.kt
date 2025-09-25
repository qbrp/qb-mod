package org.qbrp.client.engine.chat

import org.qbrp.client.engine.ClientEngine
import org.qbrp.main.core.modules.EngineModule

abstract class ClientChatAddon(name: String) : EngineModule("chat-addon-${name}") {
    init {
        dependsOn { ClientEngine.isApiAvailable<ClientChatAPI>() }
    }

    inline fun <reified T: Any> getChatLocal(): T {
        return ClientEngine.getModule<ChatModuleClient>().getLocal<T>()
    }
}