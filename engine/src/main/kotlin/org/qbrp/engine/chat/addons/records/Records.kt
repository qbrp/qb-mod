package org.qbrp.engine.chat.addons.records

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import org.koin.core.component.get
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.core.resources.ServerResources
import org.qbrp.core.resources.units.TextUnit
import org.qbrp.engine.chat.ChatAddon
import org.qbrp.engine.chat.core.events.MessageHandledEvent
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.system.modules.Autoload
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

    override fun getKoinModule(): Module = module {
        single { ServerResources.getRootBranch().records.addUnit(Record(),"chat_log_${composeRecordName()}", "json") }
        single { get<TextUnit>().data as Record }
    }

    override fun getAPI(): RecordsAPI = this

    private fun composeRecordName(): String {
        val currentTime = LocalDateTime.now(Clock.systemUTC())
        val formatter = DateTimeFormatter.ofPattern("HH-mm-ss")
        return currentTime.format(formatter)
    }

    override fun addMessage(message: ChatMessage) {
        get<Record>().addMessage(message)
        get<TextUnit>().save()
    }
}