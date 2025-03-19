package org.qbrp.engine.chat.addons.records

import com.google.gson.Gson
import net.minecraft.command.argument.UuidArgumentType.uuid
import net.minecraft.util.ActionResult
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.qbrp.core.resources.data.Data
import org.qbrp.engine.Engine
import org.qbrp.engine.chat.ChatAPI
import org.qbrp.engine.chat.core.events.MessageReceivedEvent
import org.qbrp.engine.chat.core.messages.ChatMessage

class Record: Data(), KoinComponent {
    private val lines = mutableMapOf<String, Line>()

    init {
        MessageReceivedEvent.EVENT.register { message ->
            addLine(message)
            ActionResult.PASS
        }
    }

    fun addLine(uuid: String, line: Line) {
        lines[uuid] = line
    }

    fun addLine(msg: ChatMessage, line: Line = Message(msg.authorName, msg.getText())) {
        lines[msg.uuid] = line
    }

    override fun toFile(): String = gson.toJson(lines)
}