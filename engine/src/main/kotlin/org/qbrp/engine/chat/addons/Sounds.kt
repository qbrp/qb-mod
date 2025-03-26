package org.qbrp.engine.chat.addons

import net.minecraft.registry.Registries
import net.minecraft.util.ActionResult
import net.minecraft.util.Identifier
import org.qbrp.engine.chat.ChatAddon
import org.qbrp.engine.chat.addons.records.Action
import org.qbrp.engine.chat.core.events.MessageSendEvent
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.LoadPriority

@Autoload(LoadPriority.ADDON)
class Sounds: ChatAddon("sounds") {

    override fun load() {
        super.load()
        MessageSendEvent.EVENT.register() { sender, message, receiver, networking ->
            message.getTags().getComponentData<String>("sound")?.also {
                receiver.playSound(Registries.SOUND_EVENT.get(Identifier.tryParse(it)), 1F, 1F)
            }
            ActionResult.PASS
        }
    }
}