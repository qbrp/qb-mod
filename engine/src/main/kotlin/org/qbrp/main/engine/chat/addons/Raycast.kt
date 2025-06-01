package org.qbrp.main.engine.chat.addons

import net.minecraft.util.ActionResult
import org.qbrp.main.engine.chat.ChatAddon
import org.qbrp.main.engine.chat.core.events.MessageSendEvent
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.LoadPriority

@Autoload(LoadPriority.ADDON)
class Raycast: ChatAddon("raycast") {

    override fun onLoad() {
        MessageSendEvent.register() { sender, message, receiver, networking ->
            if (message.getTags().getComponentData<Boolean>("raycast") == true) {
                if (message.getAuthorEntity()?.canSee(receiver) != true) {
                    ActionResult.FAIL
                }
            }
            ActionResult.PASS
        }
    }

}