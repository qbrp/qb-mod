package org.qbrp.engine.client.engine.chat.addons

import net.fabricmc.api.EnvType
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.engine.client.EngineClient
import org.qbrp.engine.client.engine.chat.ClientChatAPI
import org.qbrp.engine.client.engine.chat.ClientChatAddon
import org.qbrp.engine.client.engine.chat.system.LinearMessageProvider
import org.qbrp.engine.client.system.networking.ClientReceiverContext
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.LoadPriority
import org.qbrp.system.networking.ClientReceiver
import org.qbrp.system.networking.messages.Messages
import org.qbrp.system.networking.messages.types.StringContent

@Autoload(LoadPriority.ADDON, EnvType.CLIENT)
class Ignore: ClientChatAddon("ignore") {
    private val ignoreList: MutableList<String> = mutableListOf()

    fun isPlayerMentioned(message: ChatMessage, player: PlayerEntity): Boolean {
        val words = listOf<String>(player.name.string, "here", "everyone")
        return words.contains(message.getTags().getComponentData<String>("mention")) == true
    }

    override fun load() {
        val chatAPI = EngineClient.getAPI<ClientChatAPI>()!!
        ClientReceiver<ClientReceiverContext>(Messages.invokeCommand("ignore"),
            StringContent::class) { message, context, receiver ->
            val group = (message.content as StringContent).string!!
            if (!ignoreList.remove(group)) {
                ignoreList.add(group)
                chatAPI.addMessage(ChatMessage.text("<gray>Группа $group добавлена в игнор"))
            } else {
                chatAPI.addMessage(ChatMessage.text("<gray>Группа $group убрана из игнора"))
            }
            updateFilters()
            true
        }.register()
    }

    private fun updateFilters() {
        val provider = EngineClient.getAPI<ClientChatAPI>()!!.getStorage().provider
        if (provider is LinearMessageProvider) {
            provider.filters["ignore"] = { line -> !ignoreList.contains(line.message.getTags().getComponentData<String>("group"))
                    || isPlayerMentioned(line.message, MinecraftClient.getInstance().player!!) }
        }
    }
}