package org.qbrp.engine.client.render

import icyllis.modernui.core.Core
import icyllis.modernui.mc.fabric.MuiFabricApi
import org.qbrp.engine.client.render.game.chat.PlayerIconRenderer
import org.qbrp.engine.client.render.screen.Notification
class Render {
    companion object {
        fun openChatScreen() {
            //MuiFabricApi.openScreen(ChatScreen())
        }
    }
    fun initialize() {
        PlayerIconRenderer().initialize()
    }
}