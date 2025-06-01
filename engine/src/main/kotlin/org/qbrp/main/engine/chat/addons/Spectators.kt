package org.qbrp.main.engine.chat.addons

import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.world.GameMode
import org.koin.core.component.inject
import org.qbrp.main.engine.chat.ChatAddon
import org.qbrp.main.engine.chat.core.events.MessageSendEvent
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.LoadPriority

@Autoload(LoadPriority.ADDON)
class Spectators(): ChatAddon("spectators") {
    override fun onLoad() {
        MessageSendEvent.register { sender, message, receiver, _ ->
            if (message.getTags().getComponentData<Boolean>("spectators") == true
                && message.getAuthorEntity()?.interactionManager?.gameMode == GameMode.SPECTATOR
                && receiver.interactionManager.gameMode != GameMode.SPECTATOR) {
                return@register ActionResult.FAIL
            } else {
                ActionResult.PASS
            }
        }
    }
}