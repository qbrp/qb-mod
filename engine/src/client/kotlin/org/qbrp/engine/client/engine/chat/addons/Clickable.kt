package org.qbrp.engine.client.engine.chat.addons

import net.fabricmc.api.EnvType
import org.qbrp.engine.chat.ChatAddon
import org.qbrp.engine.chat.core.events.ChatFormatEvent
import org.qbrp.engine.chat.core.system.TextTagsTransformer
import org.qbrp.engine.client.EngineClient
import org.qbrp.engine.client.engine.chat.ClientChatAPI
import org.qbrp.engine.client.engine.chat.ClientChatAddon
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.LoadPriority

@Autoload(LoadPriority.ADDON, EnvType.CLIENT)
class Clickable: ClientChatAddon("clickable") {
    override fun load() {
        ChatFormatEvent.EVENT.register { message, text ->
            TextTagsTransformer.replaceTagsWithFormat(message.getText(), "playerNickClickable") { tag, value ->
                val groupPrefix = message.getTags().getComponentData<String>("group")?.let {
                    EngineClient.getAPI<ClientChatGroupsAPI>()?.getChatGroups()?.getGroup(it)?.prefix ?: ""
                } ?: ""
                "<click:suggest_command:${groupPrefix}@${value}><hover:show_text:Вставить упоминание в чат>${value}</click>"
            }
        }
    }
}