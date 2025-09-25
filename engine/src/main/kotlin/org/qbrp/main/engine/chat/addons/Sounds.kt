package org.qbrp.main.engine.chat.addons

import net.minecraft.util.ActionResult
import org.koin.core.component.get
import org.qbrp.main.core.mc.player.getEntity
import org.qbrp.main.engine.chat.ChatAddon
import org.qbrp.main.engine.chat.core.events.MessageSendEvent
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.utils.playSoundForPlayer

@Autoload(Priorities.ADDON)
class Sounds: ChatAddon("sounds") {

    override fun onLoad() {
        super.onLoad()
        MessageSendEvent.register() { sender, message, receiver, networking ->
            message.getTags().getComponentData<String>("sound")?.also {
                if (it != "") {
                    try {
                        playSoundForPlayer(receiver.uuid.getEntity(get()), it, 1F, 1F)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            ActionResult.PASS
        }
    }
}