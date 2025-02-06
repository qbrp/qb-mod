package org.qbrp.engine.client.render.hud

import icyllis.modernui.mc.fabric.MuiFabricApi
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import org.qbrp.engine.client.render.hud.chat.ChatHud
import org.qbrp.engine.client.render.hud.chat.ChatInputScreen

class Hud {
    private val chatTypingHud = ChatHud()
    private val chatInputScreen = ChatInputScreen()

    fun openChat() = MuiFabricApi.openScreen(ChatInputScreen())

    fun initialize() {
        HudRenderCallback.EVENT.register { drawContext, tickDelta ->
            chatTypingHud.render(drawContext, tickDelta)
        }
    }

}