package org.qbrp.main.engine.chat.addons

import net.minecraft.util.ActionResult
import org.qbrp.main.core.mc.player.PlayersModule
import org.qbrp.main.core.mc.player.PlayersUtil
import org.qbrp.main.engine.chat.ChatAddon
import org.qbrp.main.engine.chat.core.events.MessageSendEvent
import org.qbrp.main.core.modules.Autoload

@Autoload(0)
class PlayerBehaviourIntegration: ChatAddon("player-behaviour-integration") {

    override fun onLoad() {
        super.onLoad()
        MessageSendEvent.register() { sender, message, receiver, networking ->
            PlayersUtil.getPlayerSession(message.authorName)?.onMessageSend(sender, message) ?: ActionResult.PASS
        }
    }

}