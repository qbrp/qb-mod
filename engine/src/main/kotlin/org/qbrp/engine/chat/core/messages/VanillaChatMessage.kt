package org.qbrp.engine.chat.core.messages

import icyllis.modernui.text.SpannableString
import net.minecraft.server.PlayerManager
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import org.qbrp.core.ServerCore
import org.qbrp.engine.chat.ChatModule.Companion.SYSTEM_MESSAGE_AUTHOR
import org.qbrp.engine.chat.core.events.MessageUpdateEvent
import org.qbrp.system.networking.messages.components.ClusterBuilder
import org.qbrp.system.networking.messages.components.Cluster
import org.qbrp.system.networking.messages.types.StringContent
import org.qbrp.system.utils.format.Format.formatAsSpans
import org.qbrp.system.utils.format.Format.formatMinecraft
import java.util.UUID

class VanillaChatMessage(authorName: String, private val vanillaText: Text): ChatMessage(authorName, vanillaText.string) {
    override fun getVanillaText(): Text = vanillaText

    companion object {
        fun create(text: Text, author: String = SYSTEM_MESSAGE_AUTHOR): ChatMessage {
            return VanillaChatMessage(author, text)
        }
    }
}