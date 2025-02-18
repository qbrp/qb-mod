package org.qbrp.engine.chat.core.messages

import icyllis.modernui.text.SpannableString
import net.minecraft.server.PlayerManager
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.core.ServerCore
import org.qbrp.engine.chat.core.events.MessageUpdateEvent
import org.qbrp.system.networking.messages.components.ClusterBuilder
import org.qbrp.system.networking.messages.components.Cluster
import org.qbrp.system.networking.messages.types.StringContent
import org.qbrp.system.utils.format.Format.formatAsSpans
import org.qbrp.system.utils.format.Format.formatMinecraft
import java.util.UUID

data class ChatMessage(val authorName: String,
                       private var text: String,
                       private var metaTags: ChatMessageTagsBuilder = ChatMessageTagsBuilder(),
                       val uuid: String = UUID.randomUUID().toString()): ChatMessageData {
    fun getFormattedText() = text.formatMinecraft()
    fun getRawText() = text
    fun getAuthorEntity(playerManager: PlayerManager = ServerCore.server.playerManager): ServerPlayerEntity? = playerManager.getPlayer(authorName)

    fun getTags() = metaTags.build().getData()
    fun getTagsBuilder() = metaTags

    fun getText() = text

    fun setText(text: String, invokeUpdateEvent: Boolean = true) {
        val cachedText = this.text
        this.text = text
        if (invokeUpdateEvent && cachedText != text) MessageUpdateEvent.EVENT.invoker().onMessageUpdate(this)
    }

    fun setTags(tags: ClusterBuilder, invokeUpdateEvent: Boolean = true) = setTags(tags as ChatMessageTagsBuilder, invokeUpdateEvent)

    fun setTags(tags: ChatMessageTagsBuilder, invokeUpdateEvent: Boolean = true) {
        val cachedTags = this.metaTags
        this.metaTags = tags
        if (invokeUpdateEvent && cachedTags != tags) MessageUpdateEvent.EVENT.invoker().onMessageUpdate(this)
    }

    fun toCluster(): Cluster {
        val builder = ClusterBuilder()
            .component("authorName", StringContent(authorName))
            .component("uuid", StringContent(uuid))
            .component("tags", metaTags.build())
            .component("text", StringContent(text))

        return builder.build()
    }

    override fun getTextSpans(): SpannableString = text.formatAsSpans()
}