package org.qbrp.engine.chat

import org.qbrp.engine.Engine
import org.qbrp.system.modules.QbModule

abstract class ChatAddon(name: String) : QbModule("chat-addon-${name}") {
    lateinit var chatAPI: ChatAPI
    init {
        dependsOn { Engine.isApiAvailable<ChatAPI>() }
    }

    override fun onLoad() {
        chatAPI = Engine.moduleManager.getAPI<ChatAPI>()!!
        chatAPI.loadAddon(this)
    }
}