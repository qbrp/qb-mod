package org.qbrp.main.engine.chat.core.messages

interface Sender {
    fun send(message: ChatMessage)
}