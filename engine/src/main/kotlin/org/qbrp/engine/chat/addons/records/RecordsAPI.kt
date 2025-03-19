package org.qbrp.engine.chat.addons.records

import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.system.modules.ModuleAPI
import kotlin.collections.set

interface RecordsAPI: ModuleAPI {
    fun addLine(uuid: String, line: Line)
    fun addLine(msg: ChatMessage, line: Line = Message(msg.authorName, msg.getText()))
    fun saveRecord()
}