package org.qbrp.engine.chat

import org.qbrp.engine.Engine
import org.qbrp.system.modules.QbModule

abstract class ChatAddon(name: String) : QbModule("chat-addon-${name}") {
    init {
        dependsOn { Engine.isApiAvailable<ChatAPI>() }
    }

    override fun load() {
        val chatAPI = Engine.moduleManager.getAPI<ChatAPI>()
        chatAPI?.loadAddon(this)
    }
}