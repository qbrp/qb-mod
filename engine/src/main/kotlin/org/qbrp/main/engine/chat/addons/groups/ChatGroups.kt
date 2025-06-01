package org.qbrp.main.engine.chat.addons.groups

import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import org.koin.core.component.get
import org.koin.core.component.inject
import org.qbrp.deprecated.resources.data.config.ConfigInitializationCallback
import org.qbrp.deprecated.resources.data.config.ServerConfigData
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.chat.ChatAddon
import org.qbrp.main.engine.chat.addons.tools.MessageTextTools
import org.qbrp.main.engine.chat.core.events.MessageReceivedEvent
import org.qbrp.main.engine.chat.core.events.MessageSenderPipeline
import org.qbrp.main.engine.chat.core.messages.ChatException
import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.engine.chat.core.system.ChatGroup
import org.qbrp.main.engine.chat.core.system.ChatGroupsStorage
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.utils.networking.info.ServerInfoAPI
import org.qbrp.main.core.utils.networking.messages.components.ClusterEntry
import org.qbrp.main.core.utils.networking.messages.types.ClusterListContent

@Autoload(priority = 1)
class ChatGroups(): ChatAddon("chat-groups"), ChatGroupsAPI {
    val server: MinecraftServer by inject()
    private lateinit var config: ServerConfigData.Chat
    private lateinit var storage: ChatGroupsStorage

    override fun getKoinModule() = inner<ChatGroupsAPI>(this) {
        scoped { ChatGroupsStorage() }
        scoped { this }
    }

    override fun onLoad() {
        super.onLoad()
        storage = getLocal()
        ConfigInitializationCallback.EVENT.register { updatedConfig ->
            config = updatedConfig.chat
            initGroups()
        }
        MessageReceivedEvent.EVENT.register { message ->
            if (message.getTags().getComponentData<Boolean>("groupHandled") != true) {
                handle(
                    message,
                    fetchGroup(message) ?: return@register ActionResult.FAIL,
                    storage.getGroup("default")!!
                )
            }
            ActionResult.PASS
        }
        MessageSenderPipeline.EVENT.register { message, sender ->
            val groupTag = message.getTags().getComponentData<String>("group")
            val group = storage.getGroup(groupTag ?: "default")
            if (message.getAuthorEntity() != null) {
                sender.addTargets(group!!.getPlayersCanSee(message.getAuthorEntity() as ServerPlayerEntity) as List<ServerPlayerEntity>)
                sender.addTarget(message.getAuthorEntity() as ServerPlayerEntity)
            }
            ActionResult.PASS
        }
    }

    companion object {
        fun handle(message: ChatMessage, group: ChatGroup, defaultGroup: ChatGroup = Engine.getAPI<ChatGroupsAPI>()
            ?.getGroup("default")!!): ActionResult {
            val author = message.getAuthorEntity()
            var sendGroup = group

            author?.let {
                if (!group.playerCanWrite(it)) sendGroup = defaultGroup

                if (!sendGroup.cooldownPassedFor(it)) {
                    ChatException(message, "Кулдаун не пройден. Подождите ${sendGroup.getEstimatedCooldown(author) / 10} секунд.").send()
                    return ActionResult.FAIL
                }
                sendGroup.cooldownPlayer(author)
            }

            if (message.getText().startsWith(sendGroup.prefix)) {
                message.setText(message.getText().substring(sendGroup.prefix.length), false)
            }

            MessageTextTools.initializeContentMessage(message)
            message.setText(sendGroup.format)
            message.getTagsBuilder()
                .component("group", sendGroup.name)
                .components(sendGroup.getDefaultComponents())
            return ActionResult.PASS
        }
        val CHAT_GROUPS_ENTRY = ClusterEntry<List<ChatGroups>>("engine.chatGroups")
    }

    override fun fetchGroup(message: ChatMessage): ChatGroup? {
        val groupTag = message.getTags().getComponentData<String>("group")
        val group = storage.getGroup(groupTag ?: "default")
        if (group == null) {
            ChatException(message, "Группа $groupTag не найдена. Доступные группы: ${storage.getAllGroups().map { it.name }.joinToString(", ")}").send()
            return null
        } else {
            return group
        }
    }

    override fun getGroup(name: String): ChatGroup? {
        return storage.getGroup(name)
    }

    override fun getStorage(): ChatGroupsStorage = this.storage

    override fun addGroup(group: ChatGroup) { storage.addGroup(group) }

    private fun initGroups() {
        storage.loadGroups(config.chatGroups)
        composeGroupsList()
    }

    private fun composeGroupsList() {
        val infoAPI = get<ServerInfoAPI>()
        val clusterGroups = storage.getAllGroups().map { group -> group.toCluster() }
        infoAPI.COMPOSER.component(CHAT_GROUPS_ENTRY, ClusterListContent().apply { list = clusterGroups } )
        infoAPI.broadcast()
    }

}