package org.qbrp.engine.client.engine.chat.system

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.hud.ChatHudLine
import net.minecraft.client.gui.hud.MessageIndicator
import net.minecraft.client.util.ChatMessages
import net.minecraft.text.Text
import net.minecraft.util.math.MathHelper
import org.qbrp.engine.chat.addons.MessageIcon
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.system.utils.format.Format.formatMinecraft

interface Provider {
    fun provide(storage: MessageStorage): MutableList<ChatHudLine.Visible>
    fun onMessageAdded(message: ChatMessage, storage: MessageStorage)
    fun onMessageEdited(uuid: String, message: ChatMessage, storage: MessageStorage)
    fun onMessageDeleted(uuid: String, storage: MessageStorage)
    fun onClear(storage: MessageStorage)

    companion object {
        fun buildChatHudLines(text: Text, icon: MessageIcon?, time: Int = (MinecraftClient.getInstance().inGameHud?.ticks ?: 0).toInt()): List<ChatHudLine.Visible> {
            var width = 300.0
            MinecraftClient.getInstance()?.inGameHud?.chatHud?.let {
                width = it.width.toDouble() / it.chatScale
            }

            val lines = ChatMessages.breakRenderedChatMessageLines(
                text,
                MathHelper.floor(width),
                MinecraftClient.getInstance().textRenderer
            )
            return lines.mapIndexed { index, orderedText ->
                val indicator = if (icon != null) MessageIndicator(icon.color, MessageIndicator.Icon.valueOf(icon.type), icon.text.formatMinecraft(), icon.type) else null
                ChatHudLine.Visible(
                    time,
                    orderedText,
                    indicator,
                    index == lines.lastIndex
                )
            }.reversed()
        }

        fun buildChatHudLines(message: ChatMessage): List<ChatHudLine.Visible> {
            return buildChatHudLines(message.getVanillaText(), MessageIcon.getMessageIcon(message))
        }
    }
}