package org.qbrp.engine.chat.addons

import net.minecraft.util.ActionResult
import org.qbrp.engine.chat.ChatAddon
import org.qbrp.engine.chat.core.events.MessageSendEvent
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.LoadPriority
import org.qbrp.system.utils.world.playSoundForPlayer

@Autoload(LoadPriority.ADDON)
class Sounds: ChatAddon("sounds") {

    override fun load() {
        super.load()
        MessageSendEvent.register() { sender, message, receiver, networking ->
            message.getTags().getComponentData<String>("sound")?.also {
                if (it != "") {
                    try {
                        playSoundForPlayer(receiver, it, 1F, 1F)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            ActionResult.PASS
        }
    }
}