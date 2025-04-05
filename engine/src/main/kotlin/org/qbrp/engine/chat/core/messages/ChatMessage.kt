package org.qbrp.engine.chat.core.messages

import icyllis.modernui.text.SpannableString
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import net.minecraft.server.PlayerManager
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import org.qbrp.core.ServerCore
import org.qbrp.engine.chat.ChatModule.Companion.SYSTEM_MESSAGE_AUTHOR
import org.qbrp.engine.chat.core.events.ChatFormatEvent
import org.qbrp.engine.chat.core.events.MessageUpdateEvent
import org.qbrp.system.networking.messages.components.ClusterBuilder
import org.qbrp.system.networking.messages.components.Cluster
import org.qbrp.system.networking.messages.types.IntContent
import org.qbrp.system.networking.messages.types.LongContent
import org.qbrp.system.networking.messages.types.StringContent
import org.qbrp.system.utils.format.Format.asMiniMessage
import org.qbrp.system.utils.format.Format.miniMessage
import org.qbrp.system.utils.format.Format.formatAsSpans
import java.util.UUID

open class ChatMessage(val authorName: String,
                       private var text: String,
                       val uuid: String = UUID.randomUUID().toString(),
                       val timestamp: Long = System.currentTimeMillis()): ChatMessageData {
    protected var metaTags: ChatMessageTagsBuilder = ChatMessageTagsBuilder()

    override fun toString(): String {
        return "ChatMessage(authorName='$authorName', text='$text')"
    }

    fun getRawText() = text
    fun getAuthorEntity(playerManager: PlayerManager = ServerCore.server.playerManager): ServerPlayerEntity? = playerManager.getPlayer(authorName)

    fun getTags() = metaTags.build().getData()
    fun getTagsBuilder() = metaTags

    fun getText() = text
    override fun getVanillaText(): Text {
        val msg = this.copy()
        return ChatFormatEvent.EVENT.invoker().handleMessage(msg, getText()).asMiniMessage()
    }

    fun setText(text: String, invokeUpdateEvent: Boolean = true) {
        val cachedText = this.text
        this.text = text
        if (invokeUpdateEvent && cachedText != text && ServerCore.isServer()) MessageUpdateEvent.EVENT.invoker().onMessageUpdate(this)
    }

    fun setText(lambda: (String) -> String, invokeUpdateEvent: Boolean = true) {
        setText(lambda(getText()), invokeUpdateEvent)
    }

    fun setHandleVanilla(enabled: Boolean) {
        getTagsBuilder().component("handleVanilla", enabled)
    }

    fun handleVanilla(): Boolean {
        return (getTags().getComponentData<Boolean>("handleVanilla") == true)
    }

    fun setTags(tags: ClusterBuilder, invokeUpdateEvent: Boolean = true) = setTags(tags as ChatMessageTagsBuilder, invokeUpdateEvent)

    fun setTags(tags: ChatMessageTagsBuilder, invokeUpdateEvent: Boolean = true) {
        val cachedTags = this.metaTags
        metaTags.override(true)
        this.metaTags = tags
        if (invokeUpdateEvent && cachedTags != tags && ServerCore.isServer()) MessageUpdateEvent.EVENT.invoker().onMessageUpdate(this)
    }

    fun handleUpdate() = MessageUpdateEvent.EVENT.invoker().onMessageUpdate(this)

    fun toCluster(): Cluster {
        val builder = ClusterBuilder()
            .component("authorName", StringContent(authorName))
            .component("uuid", StringContent(uuid))
            .component("tags", metaTags.build())
            .component("text", StringContent(text))
            .component("timestamp", LongContent(timestamp))

        return builder.build()
    }

    fun copy(): ChatMessage {
        val tagsBuilder = metaTags.copy()
        return ChatMessage(authorName, text, uuid).apply { setTags(tagsBuilder, false) }
    }

    fun sendException(text: String) {
        ChatException(this, text).send()
    }

    override fun getTextSpans(): SpannableString = text.formatAsSpans()

    companion object {
        fun create(text: Text, author: String = SYSTEM_MESSAGE_AUTHOR): ChatMessage {
            return ChatMessage(author, text.string)
        }
        fun text(text: String): ChatMessage {
            return ChatMessage(SYSTEM_MESSAGE_AUTHOR, text)
        }
    }
}