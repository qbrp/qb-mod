package org.qbrp.engine.chat.addons

import net.minecraft.util.ActionResult
import org.qbrp.core.mc.player.PlayerManager
import org.qbrp.engine.chat.ChatAddon
import org.qbrp.engine.chat.core.events.MessageSendEvent
import org.qbrp.system.modules.Autoload

@Autoload(0)
class PlayerBehaviourIntegration: ChatAddon("player-behaviour-integration") {

    override fun load() {
        super.load()
        MessageSendEvent.register() { sender, message, receiver, networking ->
            PlayerManager.getPlayerSession(message.authorName)?.onMessageSend(sender, message) ?: ActionResult.PASS
        }
    }

}