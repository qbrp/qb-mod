package org.qbrp.engine.client.engine.chat.addons

import net.fabricmc.api.EnvType
import net.minecraft.client.MinecraftClient
import org.qbrp.engine.chat.ChatAddon
import org.qbrp.engine.client.engine.chat.ClientChatAddon
import org.qbrp.engine.client.engine.chat.system.events.TextUpdateCallback
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.LoadPriority

@Autoload(LoadPriority.ADDON, EnvType.CLIENT)
class Static: ClientChatAddon("static") {
    override fun load() {
        TextUpdateCallback.EVENT.register { text, message ->
            if (message.message.getTags().getComponentData<Boolean>("static") == true) {
                message.update(message.message,MinecraftClient.getInstance().inGameHud?.ticks ?: 0)
            } else {
                text
            }
        }
    }
}