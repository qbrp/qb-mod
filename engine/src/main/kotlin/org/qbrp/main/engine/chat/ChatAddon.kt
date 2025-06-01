package org.qbrp.main.engine.chat

import org.qbrp.main.engine.Engine
import org.qbrp.main.core.modules.QbModule

abstract class ChatAddon(name: String) : QbModule("chat-addon-${name}") {
    lateinit var chatAPI: ChatAPI
    init {
        dependsOn { Engine.isApiAvailable<ChatAPI>() }
    }

    override fun onLoad() {
        chatAPI = Engine.getAPI<ChatAPI>()!!
        chatAPI.loadAddon(this)
    }
}