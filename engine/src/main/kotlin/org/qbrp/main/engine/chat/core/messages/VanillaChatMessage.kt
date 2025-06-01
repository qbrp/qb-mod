package org.qbrp.main.engine.chat.core.messages

import icyllis.modernui.text.SpannableString
import net.minecraft.server.PlayerManager
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import org.qbrp.main.core.Core
import org.qbrp.main.engine.chat.ChatModule.Companion.SYSTEM_MESSAGE_AUTHOR
import org.qbrp.main.engine.chat.core.events.MessageUpdateEvent
import org.qbrp.main.core.utils.networking.messages.components.ClusterBuilder
import org.qbrp.main.core.utils.networking.messages.components.Cluster
import org.qbrp.main.core.utils.networking.messages.types.StringContent
import org.qbrp.main.core.utils.format.Format.formatAsSpans
import org.qbrp.main.core.utils.format.Format.formatMinecraft
import java.util.UUID

class VanillaChatMessage(authorName: String, private val vanillaText: Text): ChatMessage(authorName, vanillaText.string) {
    override fun getVanillaText(): Text = vanillaText

    companion object {
        fun create(text: Text, author: String = SYSTEM_MESSAGE_AUTHOR): ChatMessage {
            return VanillaChatMessage(author, text)
        }
    }
}