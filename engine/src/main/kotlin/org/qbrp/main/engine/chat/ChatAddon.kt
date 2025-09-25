package org.qbrp.main.engine.chat

import org.qbrp.main.engine.Engine
import org.qbrp.main.core.modules.EngineModule

abstract class ChatAddon(name: String) : EngineModule("chat-addon-${name}") {
    lateinit var chatAPI: ChatAPI
    init {
        dependsOn { Engine.isApiAvailable<ChatAPI>() }
    }

    override fun onLoad() {
        chatAPI = Engine.getAPI<ChatAPI>()!!
        chatAPI.loadAddon(this)
    }
}