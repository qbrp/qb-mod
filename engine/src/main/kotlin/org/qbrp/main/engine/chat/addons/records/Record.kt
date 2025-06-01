package org.qbrp.main.engine.chat.addons.records

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import org.koin.core.component.KoinComponent
import org.qbrp.deprecated.resources.data.Data
import org.qbrp.main.engine.chat.core.messages.ChatMessage

class Record: Data(), KoinComponent {
    private val messages = mutableListOf<JsonNode>()

    fun addMessage(message: ChatMessage) {
        messages.add(MAPPER.valueToTree(message))
    }

    override fun toFile(): String = MAPPER
        .writerWithDefaultPrettyPrinter()      // опционально
        .writeValueAsString(messages)

    companion object {
        private val MAPPER = ObjectMapper().apply {
            registerModule(SimpleModule().apply {
                addSerializer(ChatMessage::class.java, ChatMessageSerializer())
            })
        }
    }
}