package org.qbrp.client.engine.chat.addons

import net.fabricmc.api.EnvType
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.client.ClientCore
import org.qbrp.client.engine.chat.ClientChatAPI
import org.qbrp.client.engine.chat.ClientChatAddon
import org.qbrp.client.engine.chat.system.LinearMessageProvider
import org.qbrp.client.core.networking.ClientReceiverContext
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.LoadPriority
import org.qbrp.main.core.utils.networking.ClientReceiver
import org.qbrp.main.core.utils.networking.messages.Messages
import org.qbrp.main.core.utils.networking.messages.types.Signal
import org.qbrp.main.core.utils.networking.messages.types.StringContent

@Autoload(LoadPriority.ADDON, EnvType.CLIENT)
class Ignore: ClientChatAddon("ignore") {
    private val ignoreList: MutableList<String> = mutableListOf()
    private var ignoreSpy = false

    fun isPlayerMentioned(message: ChatMessage, player: PlayerEntity): Boolean {
        val words = listOf<String>(player.name.string, "here", "everyone")
        return words.contains(message.getTags().getComponentData<String>("mention")) == true
    }

    override fun onLoad() {
        val chatAPI = ClientCore.getAPI<ClientChatAPI>()!!
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
        ClientReceiver<ClientReceiverContext>(Messages.invokeCommand("spy"),
            Signal::class) { message, context, receiver ->
            ignoreSpy = !ignoreSpy
            if (!ignoreSpy) {
                chatAPI.addMessage(ChatMessage.text("<gray>Слежка включена"))
            } else {
                chatAPI.addMessage(ChatMessage.text("<gray>Слежка выключена"))
            }
            updateFilters()
            true
        }.register()
    }

    private fun updateFilters() {
        val provider = ClientCore.getAPI<ClientChatAPI>()!!.getStorage().provider
        if (provider is LinearMessageProvider) {
            provider.filters["ignoreGroup"] = { line -> !ignoreList.contains(line.message.getTags().getComponentData<String>("group"))
                    || isPlayerMentioned(line.message, MinecraftClient.getInstance().player!!) }
            provider.filters["ignoreSpy"] = { line ->
                if (ignoreSpy) {
                    line.message.getTags().getComponentData<Boolean>("spy") != true
                } else {
                    true // не фильтруем ничего
                }
            }
        }
    }
}