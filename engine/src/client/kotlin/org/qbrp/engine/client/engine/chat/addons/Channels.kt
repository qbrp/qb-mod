package org.qbrp.engine.client.engine.chat.addons

import net.fabricmc.api.EnvType
import net.minecraft.util.ActionResult
import org.koin.core.component.get
import org.qbrp.engine.chat.ChatAddon
import org.qbrp.engine.client.engine.chat.ClientChatAddon
import org.qbrp.engine.client.engine.chat.system.LinearMessageProvider
import org.qbrp.engine.client.engine.chat.system.MessageStorage
import org.qbrp.engine.client.engine.chat.system.Provider
import org.qbrp.engine.client.engine.chat.system.events.MessageAddedEvent
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.LoadPriority

@Autoload(LoadPriority.ADDON, EnvType.CLIENT)
class Channels: ClientChatAddon("channels") {
    private lateinit var providersCache: MutableMap<String, Provider>

    override fun load() {
        providersCache = mutableMapOf<String, Provider>("default" to get<MessageStorage>().provider)
        MessageAddedEvent.EVENT.register { message, storage ->
            val channel = message.getTags().getComponentData<String>("channel") ?: return@register ActionResult.PASS
            val provider = providersCache[channel] ?: run {
                val newProvider =
                    LinearMessageProvider { it.message.getTags().getComponentData<String>("channel") == channel }
                providersCache[channel] = newProvider
                newProvider
            }
            get<MessageStorage>().provider = provider
            ActionResult.PASS
        }
    }
}