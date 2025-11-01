package org.qbrp.client.engine.chat.addons

import net.fabricmc.api.EnvType
import net.minecraft.util.ActionResult
import org.koin.dsl.module
import org.qbrp.main.engine.chat.core.system.ChatGroup
import org.qbrp.main.engine.chat.core.system.ChatGroupsStorage
import org.qbrp.client.engine.chat.ClientChatAddon
import org.qbrp.client.engine.chat.system.events.MessageSendEvent
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.modules.ModuleAPI

@Autoload(Priorities.ADDON, EnvType.CLIENT)
class Groups : ClientChatAddon("groups"), ClientChatGroupsAPI {
    override fun getKoinModule() = module {
        single<ClientChatGroupsAPI> { this@Groups }
        single { ChatGroupsStorage() }
   }

    override fun getAPI(): ModuleAPI? = this

    override fun loadChatGroups(groups: List<ChatGroup>) {
        getLocal<ChatGroupsStorage>().loadGroups(groups)
    }

    override fun getChatGroups(): ChatGroupsStorage {
        return getLocal<ChatGroupsStorage>()
    }

    override fun onEnable() {
        MessageSendEvent.EVENT.register { message ->
            for (group in getLocal<ChatGroupsStorage>().getAllGroups()) {
                if (group.isInMessage(message) && group.name != "default") {
                    message.apply {
                        setTags(
                            getTagsBuilder()
                                .component("group", group.name)
                        )
                    }
                    break
                }
            }
            ActionResult.PASS
        }
    }
}