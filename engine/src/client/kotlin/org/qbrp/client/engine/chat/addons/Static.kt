package org.qbrp.client.engine.chat.addons

import net.fabricmc.api.EnvType
import net.minecraft.client.MinecraftClient
import org.qbrp.main.engine.chat.ChatAddon
import org.qbrp.client.engine.chat.ClientChatAddon
import org.qbrp.client.engine.chat.system.events.TextUpdateCallback
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.LoadPriority

@Autoload(LoadPriority.ADDON, EnvType.CLIENT)
class Static: ClientChatAddon("static") {
    override fun onLoad() {
        TextUpdateCallback.EVENT.register { text, message ->
            if (message.message.getTags().getComponentData<Boolean>("static") == true) {
                message.update(message.message,MinecraftClient.getInstance().inGameHud?.ticks ?: 0)
            } else {
                text
            }
        }
    }
}