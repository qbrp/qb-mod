package org.qbrp.main.engine.chat.addons

import net.minecraft.util.ActionResult
import org.qbrp.main.core.mc.player.gamemode.MinecraftGameMode
import org.qbrp.main.engine.chat.ChatAddon
import org.qbrp.main.engine.chat.core.events.MessageSendEvent
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities

@Autoload(Priorities.ADDON)
class Spectators(): ChatAddon("spectators") {
    override fun onLoad() {
        MessageSendEvent.register { sender, message, receiver, _ ->
            if (message.getTags().getComponentData<Boolean>("spectators") == true
                && message.author?.serverState?.gameMode == MinecraftGameMode.SPECTATOR
                && receiver.serverState.gameMode != MinecraftGameMode.SPECTATOR) {
                return@register ActionResult.FAIL
            } else {
                ActionResult.PASS
            }
        }
    }
}