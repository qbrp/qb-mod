package org.qbrp.engine.chat.addons.groups

import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.dsl.module
import org.qbrp.core.resources.data.config.ConfigInitializationCallback
import org.qbrp.core.resources.data.config.ServerConfigData
import org.qbrp.engine.Engine
import org.qbrp.engine.chat.ChatAddon
import org.qbrp.engine.chat.addons.tools.MessageTextTools
import org.qbrp.engine.chat.core.events.MessageReceivedEvent
import org.qbrp.engine.chat.core.events.MessageSenderPipeline
import org.qbrp.engine.chat.core.messages.ChatException
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.engine.chat.core.system.ChatGroup
import org.qbrp.engine.chat.core.system.ChatGroupsStorage
import org.qbrp.system.modules.Autoload
import org.qbrp.system.networking.ServerInformation
import org.qbrp.system.networking.messages.types.ClusterListContent

@Autoload(priority = 1)
class ChatGroups(): ChatAddon("chat-groups"), ChatGroupsAPI {
    val server: MinecraftServer by inject()
    private var config: ServerConfigData.Chat = get()
    private lateinit var storage: ChatGroupsStorage

    override fun getKoinModule() = module {
        single { ChatGroupsStorage() }
        single { this }
    }

    override fun getAPI(): ChatGroupsAPI = this

    override fun load() {
        super.load()
        storage = get()
        ConfigInitializationCallback.EVENT.register { updatedConfig ->
            config = updatedConfig.chat
            initGroups()
        }
        initGroups()

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

    override fun addGroup(group: ChatGroup) { storage.addGroup(group) }

    private fun initGroups() {
        storage.loadGroups(config.chatGroups)
        composeGroupsList()
        ServerInformation.send()
    }

    private fun composeGroupsList() {
        val clusterGroups = storage.getAllGroups().map { group -> group.toCluster() }
        ServerInformation.COMPOSER.component("engine.chatGroups", ClusterListContent().apply { list = clusterGroups } )
    }

}