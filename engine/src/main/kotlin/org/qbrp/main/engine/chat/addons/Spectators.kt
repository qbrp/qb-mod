package org.qbrp.main.engine.chat.addons

import net.minecraft.util.ActionResult
import net.minecraft.world.GameMode
import org.qbrp.main.engine.chat.ChatAddon
import org.qbrp.main.engine.chat.core.events.MessageSendEvent
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities

@Autoload(Priorities.ADDON)
class Spectators(): ChatAddon("spectators") {
    override fun onLoad() {
        MessageSendEvent.register { sender, message, receiver, receiverEntity, _ ->
            if (message.getTags().getComponentData<Boolean>("spectators") == true
                && message.getAuthorEntity()?.interactionManager?.gameMode == GameMode.SPECTATOR
                && receiverEntity.interactionManager.gameMode != GameMode.SPECTATOR) {
                return@register ActionResult.FAIL
            } else {
                ActionResult.PASS
            }
        }
    }
}