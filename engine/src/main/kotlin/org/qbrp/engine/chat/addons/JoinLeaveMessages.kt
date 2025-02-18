package org.qbrp.engine.chat.addons

import org.qbrp.core.resources.data.config.ServerConfigData
import org.qbrp.engine.Engine
import org.qbrp.engine.chat.ChatModule
import org.qbrp.engine.chat.ChatModule.Companion.MESSAGE_AUTHOR_SYSTEM
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.engine.chat.core.messages.ChatMessageTagsBuilder
import org.qbrp.engine.chat.core.messages.ChatMessageTagsCluster
import org.qbrp.system.networking.messages.types.StringContent

class JoinLeaveMessages(val config: ServerConfigData.Chat, val api: ChatModule.Api) {

    fun sendJoinMessage() {
        sendMessage("playerJoin", text = config.joinMessage)
    }

    fun sendLeaveMessage() {
        sendMessage("playerLeave", text = config.leaveMessage)
    }

    private fun sendMessage(type: String, text: String) {
        api.getBroadcaster().broadcast(ChatMessage(
            authorName = MESSAGE_AUTHOR_SYSTEM,
            text = text,
        ).apply {
            setTags(getTagsBuilder()
                .component("group", StringContent(type)) as ChatMessageTagsBuilder)
        })
    }
}