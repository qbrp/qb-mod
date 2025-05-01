package org.qbrp.engine.chat.addons

import net.minecraft.util.ActionResult
import org.qbrp.engine.chat.ChatAddon
import org.qbrp.engine.chat.core.events.MessageSendEvent
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.LoadPriority

@Autoload(LoadPriority.ADDON)
class Raycast: ChatAddon("raycast") {

    override fun load() {
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