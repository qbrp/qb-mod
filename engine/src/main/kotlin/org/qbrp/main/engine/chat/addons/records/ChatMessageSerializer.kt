package org.qbrp.main.engine.chat.addons.records

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import org.qbrp.main.engine.chat.core.messages.ChatMessage

class ChatMessageSerializer: StdSerializer<ChatMessage>(ChatMessage::class.java) {
    override fun serialize(
        msg: ChatMessage,
        json: JsonGenerator,
        p2: SerializerProvider
    ) {
        json.writeStartObject()
        json.writeFieldName("text")
        json.writeString(msg.getText())
        json.writeFieldName("author")
        json.writeString(msg.authorName)
        json.writeFieldName("tags")
        json.writeStartArray()
        msg.getTags().components.forEach {
            json.writeStartObject()
            json.writeStringField("name", it.name)
            json.writeStringField("value", it.content.toString())
            json.writeEndObject()
        }
        json.writeEndArray()
        json.writeFieldName("time")
        json.writeString(msg.timestamp.toString())
        json.writeEndObject()
    }

}