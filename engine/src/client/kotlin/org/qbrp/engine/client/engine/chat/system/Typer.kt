package org.qbrp.engine.client.engine.chat.system

import net.minecraft.client.font.TextRenderer
import org.qbrp.engine.Engine
import org.qbrp.engine.chat.core.system.ChatGroup
import org.qbrp.engine.client.EngineClient
import org.qbrp.engine.client.engine.chat.ClientChatAPI
import org.qbrp.engine.client.engine.chat.addons.ClientChatGroupsAPI
import org.qbrp.engine.client.engine.chat.system.events.ChatInputParseEvent
import org.qbrp.system.networking.messages.components.Component
import org.qbrp.system.networking.messages.types.*
import org.qbrp.system.utils.format.Format
import org.qbrp.system.utils.format.Format.formatMinecraft

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
            return renderer.getWidth(calculateMetaInfoNames().formatMinecraft().string)
        }

        fun findGroupFromTags(): ChatGroup? {
            return Engine.getAPI<ClientChatGroupsAPI>()?.getChatGroups()?.getGroup(getComponent<String>("group") ?: "")
        }
    }
}