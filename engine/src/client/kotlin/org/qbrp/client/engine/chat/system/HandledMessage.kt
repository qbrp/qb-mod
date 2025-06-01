package org.qbrp.client.engine.chat.system

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.gui.hud.ChatHudLine
import org.qbrp.main.engine.chat.addons.MessageIcon
import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.client.engine.chat.system.events.TextUpdateCallback
import org.qbrp.main.core.utils.format.Format.asMiniMessage

data class HandledMessage(var message: ChatMessage) {
    private var _text: List<ChatHudLine.Visible> = Provider.buildChatHudLines(message)

    val text: List<ChatHudLine.Visible>
        get() = TextUpdateCallback.EVENT.invoker().modifyText(_text, this) ?: _text

    fun editText(text: List<ChatHudLine.Visible>) {
        this._text = text
    }

    fun update(message: ChatMessage, time: Int = _text.first().addedTime): List<ChatHudLine.Visible> {
        return Provider.buildChatHudLines(message.getVanillaText(), MessageIcon.getMessageIcon(message), time)
    }

    fun update(text: String, icon: MessageIcon? = MessageIcon.getMessageIcon(message), time: Int = _text.first().addedTime): List<ChatHudLine.Visible> {
        return Provider.buildChatHudLines(text.asMiniMessage(), icon, time)
    }

    companion object {
        fun from(msg: ChatMessage): HandledMessage {
            return HandledMessage(msg).apply {
                _text = Provider.buildChatHudLines(msg)
            }
        }
    }
}