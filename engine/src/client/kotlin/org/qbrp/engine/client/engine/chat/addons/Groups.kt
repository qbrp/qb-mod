package org.qbrp.engine.client.engine.chat.addons

import net.fabricmc.api.EnvType
import net.minecraft.util.ActionResult
import org.koin.core.component.get
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.engine.chat.ChatAddon
import org.qbrp.engine.chat.core.system.ChatGroup
import org.qbrp.engine.chat.core.system.ChatGroupsStorage
import org.qbrp.engine.client.engine.chat.system.events.MessageSendEvent
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.LoadPriority
import org.qbrp.system.networking.messages.components.readonly.ClusterViewer

@Autoload(LoadPriority.ADDON, EnvType.CLIENT)
class Groups: ChatAddon("groups"), ClientChatGroupsAPI {
    override fun getKoinModule() = module {
        single { ChatGroupsStorage() }
    }

    override fun loadChatGroups(groups: List<ChatGroup>) {
        get<ChatGroupsStorage>().loadGroups(groups)
    }

    override fun getChatGroups(): ChatGroupsStorage {
        return get<ChatGroupsStorage>()
    }

    override fun createChatGroupFromCluster(cluster: ClusterViewer): ChatGroup {
        return ChatGroup(
            cluster.getComponentData<String>("name")!!,
            cluster.getComponentData<String>("simpleName")!!,
            cluster.getComponentData<String>("prefix")!!,
            cluster.getComponentData<String>("color")!!
        )
    }

    override fun load() {
        MessageSendEvent.EVENT.register { message ->
            for (group in get<ChatGroupsStorage>().getAllGroups()) {
                if (group.isInMessage(message) && group.name != "default") {
                    message.apply {
                        setTags(getTagsBuilder()
                            .component("group", group.name))
                    }
                    break
                }
            }
            ActionResult.PASS
        }
    }
}