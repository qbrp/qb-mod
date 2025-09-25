package org.qbrp.main.engine.chat.addons

import net.minecraft.util.ActionResult
import org.koin.core.component.get
import org.qbrp.main.core.mc.player.getEntity
import org.qbrp.main.engine.chat.ChatAddon
import org.qbrp.main.engine.chat.core.events.MessageSendEvent
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities

@Autoload(Priorities.ADDON)
class Raycast: ChatAddon("raycast") {

    override fun onLoad() {
        MessageSendEvent.register() { sender, message, receiver, networking ->
            if (message.getTags().getComponentData<Boolean>("raycast") == true) {
                if (message.getAuthorEntity()?.canSee(receiver.uuid.getEntity(get())) != true) {
                    ActionResult.FAIL
                }
            }
            ActionResult.PASS
        }
    }

}