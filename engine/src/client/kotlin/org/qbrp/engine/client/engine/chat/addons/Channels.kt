package org.qbrp.engine.client.engine.chat.addons

import net.fabricmc.api.EnvType
import net.minecraft.util.ActionResult
import org.koin.core.component.get
import org.qbrp.engine.chat.ChatAddon
import org.qbrp.engine.chat.addons.records.Action
import org.qbrp.engine.client.engine.chat.ClientChatAddon
import org.qbrp.engine.client.engine.chat.system.LinearMessageProvider
import org.qbrp.engine.client.engine.chat.system.MessageStorage
import org.qbrp.engine.client.engine.chat.system.Provider
import org.qbrp.engine.client.engine.chat.system.UIMessageProvider
import org.qbrp.engine.client.engine.chat.system.events.MessageAddedEvent
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.LoadPriority

@Autoload(LoadPriority.ADDON, EnvType.CLIENT)
class Channels : ClientChatAddon("channels") {
    private lateinit var providersCache: MutableMap<String, Provider>

    override fun load() {
        providersCache = mutableMapOf<String, Provider>("default" to get<MessageStorage>().provider)
        MessageAddedEvent.EVENT.register { message, storage ->
            providersCache.values.forEach {
                if (it != get<MessageStorage>().provider && message.getTags().getComponentData<Boolean>("static") == false)
                    it.onMessageAdded(message, storage)
            }
            message.getTags().getComponentData<String>("clearChannel")?.let {
                providersCache.remove(it)
            }
            ActionResult.PASS
        }
        MessageAddedEvent.EVENT.register { message, storage ->
            val channel = message.getTags().getComponentData<String>("channel") ?: return@register ActionResult.PASS
            val provider = providersCache[channel] ?: run {
                val newProvider = LinearMessageProvider(
                    mutableMapOf("channel" to { handledMessage ->
                        handledMessage.message.getTags().getComponentData<String>("channel") == channel
                    })
                )
                providersCache[channel] = newProvider
                newProvider
            }
            get<MessageStorage>().provider = provider
            ActionResult.PASS
        }
        MessageAddedEvent.EVENT.register { message, storage ->
            val openUiProvider = message.getTags().getComponentData<Boolean>("ui") ?: return@register ActionResult.PASS
            if (openUiProvider) {
                val name = message.getTags().getComponentData<String>("ui.name") ?: return@register ActionResult.PASS
                providersCache[name] = UIMessageProvider(name)
                get<MessageStorage>().provider = providersCache[name]!!
            } else {
                get<MessageStorage>().provider = providersCache["default"]!!
            }
            ActionResult.PASS
        }
    }
}
