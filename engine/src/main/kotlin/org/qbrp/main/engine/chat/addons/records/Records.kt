package org.qbrp.main.engine.chat.addons.records

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.main.deprecated.resources.ServerResources
import org.qbrp.deprecated.resources.units.TextUnit
import org.qbrp.main.engine.chat.ChatAddon
import org.qbrp.main.engine.chat.core.events.MessageHandledEvent
import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.core.modules.Autoload
import java.time.Clock
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Autoload(3)
class Records: ChatAddon("records"), RecordsAPI {
    init {
        MessageHandledEvent.EVENT.register { message, receivers: List<ServerPlayerEntity> ->
            addMessage(message)
            ActionResult.PASS
        }
    }

    override fun getKoinModule(): Module = inner<RecordsAPI>(this) {
        scoped { ServerResources.getRootBranch().records.addUnit(Record(),"chat_log_${composeRecordName()}", "json") }
        scoped { get<TextUnit>().data as Record }
    }

    private fun composeRecordName(): String {
        val currentTime = LocalDateTime.now(Clock.systemUTC())
        val formatter = DateTimeFormatter.ofPattern("HH-mm-ss")
        return currentTime.format(formatter)
    }

    override fun addMessage(message: ChatMessage) {
        getLocal<Record>().addMessage(message)
        getLocal<TextUnit>().save()
    }
}