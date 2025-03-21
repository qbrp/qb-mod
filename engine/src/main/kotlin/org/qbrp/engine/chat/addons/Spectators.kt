package org.qbrp.engine.chat.addons

import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.world.GameMode
import org.koin.core.component.inject
import org.qbrp.engine.chat.ChatAddon
import org.qbrp.engine.chat.core.events.MessageSendEvent
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.LoadPriority

@Autoload(LoadPriority.ADDON)
class Spectators(): ChatAddon("spectators") {
    override fun load() {
        MessageSendEvent.EVENT.register { sender, message, receiver, _ ->
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