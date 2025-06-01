package org.qbrp.client.engine.chat.addons

import net.fabricmc.api.EnvType
import net.minecraft.util.ActionResult
import org.qbrp.client.engine.chat.ClientChatAddon
import org.qbrp.client.engine.chat.system.LinearMessageProvider
import org.qbrp.client.engine.chat.system.MessageStorage
import org.qbrp.client.engine.chat.system.Provider
import org.qbrp.client.engine.chat.system.UIMessageProvider
import org.qbrp.client.engine.chat.system.events.MessageAddedEvent
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.LoadPriority

@Autoload(LoadPriority.ADDON, EnvType.CLIENT)
class Channels : ClientChatAddon("channels") {
    private lateinit var providersCache: MutableMap<String, Provider>
    private lateinit var messageStorage: MessageStorage

    override fun onLoad() {
        messageStorage = getChatLocal()
        providersCache = mutableMapOf<String, Provider>("default" to messageStorage.provider)
        MessageAddedEvent.EVENT.register { message, storage ->
            providersCache.values.forEach {
                if (it != messageStorage.provider && message.getTags().getComponentData<Boolean>("static") == false)
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
            messageStorage.provider = provider
            ActionResult.PASS
        }
        MessageAddedEvent.EVENT.register { message, storage ->
            val openUiProvider = message.getTags().getComponentData<Boolean>("ui") ?: return@register ActionResult.PASS
            if (openUiProvider) {
                val name = message.getTags().getComponentData<String>("ui.name") ?: return@register ActionResult.PASS
                providersCache[name] = UIMessageProvider(name)
                messageStorage.provider = providersCache[name]!!
            } else {
                messageStorage.provider = providersCache["default"]!!
            }
            ActionResult.PASS
        }
    }
}
