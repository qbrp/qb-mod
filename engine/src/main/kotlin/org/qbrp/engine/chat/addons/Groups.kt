package org.qbrp.engine.chat.addons

import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import org.qbrp.core.resources.data.config.ServerConfigData
import org.qbrp.engine.chat.core.events.MessageSenderPipeline
import org.qbrp.engine.chat.core.messages.ChatException
import org.qbrp.engine.chat.core.system.ChatGroups

class Groups(val server: MinecraftServer, val config: ServerConfigData.Chat) {
    val groupsList = ChatGroups()

    init {
        groupsList.loadGroups(config.chatGroups)
        MessageSenderPipeline.EVENT.register { message, sender ->
            val groupTag = message.getTags().getComponentData<String>("group")
            val group = groupsList.getGroup(groupTag ?: "default")
            val author = message.getAuthorEntity(server.playerManager)
            if (group == null) {
                return@register ChatException(message, "Группа $groupTag не найдена. Доступные группы: ${groupsList.getAllGroups().map { it.name }.joinToString(", ")}").send()
            }

            message.getTagsBuilder()
                .placeholder("playerName", author!!.name.string)
                .placeholder("playerDisplayName", author.displayName.string)
                .placeholder("text", message.text)
            message.text = group.format

            sender.addTargets(group.getPlayersCanSee(message.getAuthorEntity() as ServerPlayerEntity) as List<ServerPlayerEntity>)
            ActionResult.PASS
        }
    }
}