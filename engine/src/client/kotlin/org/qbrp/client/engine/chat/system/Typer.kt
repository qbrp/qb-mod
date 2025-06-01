package org.qbrp.client.engine.chat.system

import net.minecraft.client.font.TextRenderer
import org.qbrp.main.engine.chat.core.system.ChatGroup
import org.qbrp.client.ClientCore
import org.qbrp.client.engine.chat.addons.ClientChatGroupsAPI
import org.qbrp.client.engine.chat.system.events.ChatInputParseEvent
import org.qbrp.main.core.utils.networking.messages.components.Component
import org.qbrp.main.core.utils.networking.messages.types.*
import org.qbrp.main.core.utils.format.Format
import org.qbrp.main.core.utils.format.Format.asMiniMessage
import org.qbrp.main.core.utils.format.Format.formatMinecraft

class Typer() {

    fun suggestShortcuts(text: String): String {
        return text
    }

    fun handleTypingMessage(text: String): TypingMessageContext {
        val context = TypingMessageContext(originalText = text, processedText = text, tags = parseTags(text))
        ChatInputParseEvent.EVENT.invoker().handleMessage(text, context)
        return context
    }

    fun parseTags(input: String): List<Component> {
        val regex = Regex("""<([a-zA-Z0-9]+):(?:"(.*?)"|([0-9]+))>""")
        return regex.findAll(suggestShortcuts(Format.stripAllFormatting(input))).map { matchResult ->
            val name = matchResult.groupValues[1]
            val stringValue = matchResult.groupValues[2]
            val numberValue = matchResult.groupValues[3]

            val value = when {
                stringValue.isNotEmpty() -> StringContent(stringValue)
                numberValue.isNotEmpty() -> numberValue.toIntOrNull()?.let { IntContent(it) } ?: StringContent("")
                else -> StringContent("").also { println("Invalid value format") }
            }
            Component(name, value)
        }.toList()
    }

    data class TypingMessageContext(
        var originalText: String,
        var processedText: String = "",
        var tags: List<Component> = emptyList()
    ) {
        fun <T> getComponent(name: String): T? {
            val tag = tags.find { it.name == name }?.content
            return if (tag is ReceiveContent) {
                @Suppress("UNCHECKED_CAST")
                tag.getData() as T
            } else {
                null
            }
        }
        fun isEmpty(): Boolean = tags.isEmpty()

        fun calculateMetaInfoNames(): String {
            val group = findGroupFromTags()
            return group?.getFormattedName() ?: ""
        }

        fun calculateMetaInfoWidth(renderer: TextRenderer): Int {
            return renderer.getWidth(calculateMetaInfoNames().asMiniMessage().string)
        }

        fun findGroupFromTags(): ChatGroup? {
            return ClientCore.getAPI<ClientChatGroupsAPI>()?.getChatGroups()?.getGroup(getComponent<String>("group") ?: "")
        }
    }
}