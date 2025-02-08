package org.qbrp.engine.chat.addons

import net.minecraft.server.MinecraftServer
import net.minecraft.util.ActionResult
import org.qbrp.engine.chat.core.events.MessageSenderPipeline

class Mentions(server: MinecraftServer) {
    init {
        MessageSenderPipeline.EVENT.register { message, sender ->
            message.getTags().getComponentData<String>("mention").let {
                sender.addTarget(server.playerManager.getPlayer(it) ?: return@register ActionResult.PASS)
            }
            ActionResult.PASS
        }
    }
}