package org.qbrp.main.engine.chat.core.messages

import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import org.koin.core.context.GlobalContext
import org.qbrp.main.core.Core
import org.qbrp.main.engine.chat.ChatModule.Companion.SYSTEM_MESSAGE_AUTHOR
import org.qbrp.main.engine.chat.core.events.ChatFormatEvent
import org.qbrp.main.engine.chat.core.events.MessageUpdateEvent
import org.qbrp.main.core.utils.networking.messages.components.ClusterBuilder
import org.qbrp.main.core.utils.networking.messages.components.Cluster
import org.qbrp.main.core.utils.networking.messages.types.LongContent
import org.qbrp.main.core.utils.networking.messages.types.StringContent
import org.qbrp.main.core.utils.format.Format.formatMm
import org.qbrp.main.engine.players.model.service.ServerPlayerStorage
import java.util.UUID

open class ChatMessage(val authorName: String,
                       private var text: String,
                       val uuid: String = UUID.randomUUID().toString(),
                       val timestamp: Long = System.currentTimeMillis()): ChatMessageData {
    protected var metaTags: ChatMessageTagsBuilder = ChatMessageTagsBuilder()
    private val playerStorage by lazy { GlobalContext.get().get<ServerPlayerStorage>() }

    override fun toString(): String {
        return "ChatMessage(authorName='$authorName', chatText='$text')"
    }

    val author get() = playerStorage.getByPlayerName(authorName)

    fun getRawText() = text
    fun getAuthorEntity(server: MinecraftServer = GlobalContext.get().get()): ServerPlayerEntity? = server.playerManager.getPlayer(authorName)

    fun getTags() = metaTags.build().getData()
    fun getTagsBuilder() = metaTags

    fun getText() = text
    override fun getVanillaText(): Text {
        val msg = this.copy()
        return ChatFormatEvent.EVENT.invoker().handleMessage(msg, getText()).formatMm()
    }

    fun setText(text: String, invokeUpdateEvent: Boolean = true) {
        val cachedText = this.text
        this.text = text
        if (invokeUpdateEvent && cachedText != text && Core.isServer()) MessageUpdateEvent.EVENT.invoker().onMessageUpdate(this)
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
        if (invokeUpdateEvent && cachedTags != tags && Core.isServer()) MessageUpdateEvent.EVENT.invoker().onMessageUpdate(this)
    }

    fun handleUpdate() = MessageUpdateEvent.EVENT.invoker().onMessageUpdate(this)

    fun toCluster(): Cluster {
        val builder = ClusterBuilder()
            .component("authorName", StringContent(authorName))
            .component("uuid", StringContent(uuid))
            .component("tags", metaTags.build())
            .component("chatText", StringContent(text))
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

    companion object {
        fun create(text: Text, author: String = SYSTEM_MESSAGE_AUTHOR): ChatMessage {
            return ChatMessage(author, text.string)
        }
        fun text(text: String): ChatMessage {
            return ChatMessage(SYSTEM_MESSAGE_AUTHOR, text)
        }
    }
}