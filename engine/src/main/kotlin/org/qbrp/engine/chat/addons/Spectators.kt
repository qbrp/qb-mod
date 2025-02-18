package org.qbrp.engine.chat.addons

import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import org.qbrp.engine.chat.core.events.MessageSendEvent

class Spectators(val server: MinecraftServer) {
    init {
        MessageSendEvent.EVENT.register { sender, message, receiver, _ ->
            if (message.getAuthorEntity(server.playerManager)?.isSpectator == true
                && message.getTags().getComponentData<Boolean>("ignoreSpectator") != true
                && receiver.isSpectator == false) {
                sender.removeTarget(receiver)
            }
            ActionResult.PASS
        }
    }
}