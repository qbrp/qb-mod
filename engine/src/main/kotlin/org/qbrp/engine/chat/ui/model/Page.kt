package org.qbrp.engine.chat.ui.model

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.engine.Engine
import org.qbrp.engine.chat.ChatAPI
import org.qbrp.engine.chat.ChatModule.Companion.SYSTEM_MESSAGE_AUTHOR
import org.qbrp.engine.chat.core.messages.ChatMessage

class Page(val name: String, text: String): ChatMessage(SYSTEM_MESSAGE_AUTHOR,
    "<aqua><bold>$name</bold></aqua> {close}<newline>$text") {
    private val chatAPI = Engine.getAPI<ChatAPI>()!!
    private val elements: MutableMap<String, Element> = mutableMapOf()

    init {
        getTagsBuilder()
            .component("ui", true)
            .component("ui.name", name)
        pasteCloseButton()
    }

    fun getOpenButton(text: String, player: ServerPlayerEntity, brackets: Boolean = true): Button {
        val openBracket = if (brackets) "(" else ""
        val closeBracket = if (brackets) ")" else ""
        return Button(
            name = "$openBracket$text<reset>$closeBracket",
            runnable = { player -> chatAPI.sendMessage(player, this) }
        ).apply { build(player) }
    }

    private fun pasteCloseButton() {
        Button(
            name = "<red>[✖]</red>",
            runnable = { player -> chatAPI.sendMessage(player, ChatMessage(SYSTEM_MESSAGE_AUTHOR, "").apply {
                getTagsBuilder()
                    .component("ui", false)
                    .component("clearChannel", name) }
                )
            },
            hover = "show_text" to "Закрыть страницу").also {
              pasteElement(it, "close")
        }
    }

    fun pasteElement(element: Element, id: String) {
        elements.put(id, element)
    }

    fun build(player: ServerPlayerEntity) {
        getTagsBuilder().apply {
            elements.forEach { key, value ->
                placeholder(key, value.apply { build(player) }.getText())
            }
        }
    }
}