package org.qbrp.engine.chat.core.messages

interface Sender {
    fun send(message: ChatMessage)
}