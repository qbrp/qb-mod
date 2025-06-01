package org.qbrp.main.engine.chat.addons

import net.minecraft.util.ActionResult
import org.qbrp.main.engine.chat.ChatAddon
import org.qbrp.main.engine.chat.core.events.MessageSendEvent
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.LoadPriority
import org.qbrp.main.core.utils.world.playSoundForPlayer

@Autoload(LoadPriority.ADDON)
class Sounds: ChatAddon("sounds") {

    override fun onLoad() {
        super.onLoad()
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