package org.qbrp.engine.chat.messages

import net.minecraft.server.PlayerManager
import org.qbrp.core.ServerCore
import org.qbrp.system.networking.messages.components.ClusterBuilder
import org.qbrp.system.networking.messages.components.Cluster
import org.qbrp.system.networking.messages.types.StringContent
import org.qbrp.system.utils.format.Format.formatMinecraft
import java.util.UUID

data class ChatMessage(val authorName: String, var text: String, var metaTags: ChatMessageTagsBuilder = ChatMessageTagsBuilder(), val uuid: String = UUID.randomUUID().toString(),) {
    fun getFormattedText() = text.formatMinecraft()
    fun getRawText() = text
    fun getAuthorEntity(playerManager: PlayerManager = ServerCore.server.playerManager) = playerManager.getPlayer(authorName)

    fun getTags() = metaTags.build().getData()
    fun getTagsBuilder() = metaTags
    fun getTextTag() {}

    fun toCluster(): Cluster {
        val builder = ClusterBuilder()
            .component("authorName", StringContent(authorName))
            .component("uuid", StringContent(uuid))
            .component("tags", metaTags.build())
            .component("text", StringContent(text))

        return builder.build()
    }
}