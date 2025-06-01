package org.qbrp.client.engine.chat.addons

import net.fabricmc.api.EnvType
import org.qbrp.main.engine.chat.core.events.ChatFormatEvent
import org.qbrp.main.engine.chat.core.system.TextTagsTransformer
import org.qbrp.client.ClientCore
import org.qbrp.client.engine.chat.ClientChatAddon
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.LoadPriority

@Autoload(LoadPriority.ADDON, EnvType.CLIENT)
class Clickable: ClientChatAddon("clickable") {
    override fun onLoad() {
        ChatFormatEvent.EVENT.register { message, text ->
            TextTagsTransformer.replaceTagsWithFormat(message.getText(), "player_nick_clickable") { tag, value ->
                val groupPrefix = message.getTags().getComponentData<String>("group")?.let {
                    ClientCore.getAPI<ClientChatGroupsAPI>()?.getChatGroups()?.getGroup(it)?.prefix ?: ""
                } ?: ""
                "<click:suggest_command:${groupPrefix}@${value}><hover:show_text:Вставить упоминание в чат>${value}</click>"
            }
        }
    }
}