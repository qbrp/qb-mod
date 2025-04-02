package org.qbrp.engine.chat.addons.records

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import org.koin.core.component.get
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.core.game.registry.CommandsRepository
import org.qbrp.core.game.registry.ServerModCommand
import org.qbrp.core.resources.ServerResources
import org.qbrp.core.resources.units.TextUnit
import org.qbrp.engine.chat.ChatAddon
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.LoadPriority
import org.qbrp.system.modules.ModuleAPI
import java.time.Clock
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Autoload(3)
class Records: ChatAddon("records"), RecordsAPI {

    override fun getKoinModule(): Module = module {
        single { ServerResources.getRootBranch().records.addUnit(Record(),"chat_log_${composeRecordName()}", "json") }
        single { get<TextUnit>().data as Record }
    }

    override fun getAPI(): RecordsAPI = this

    private fun composeRecordName(): String {
        val currentTime = LocalDateTime.now(Clock.systemUTC())
        val formatter = DateTimeFormatter.ofPattern("HH-mm-ss")
        return  currentTime.format(formatter)
    }

    override fun saveRecord() {
        get<TextUnit>().save()
    }

    override fun addLine(uuid: String, line: Line) {
        get<Record>().addLine(uuid, line)
        saveRecord()
    }

    override fun addLine(msg: ChatMessage, line: Line) {
        get<Record>().addLine(msg, line)
        saveRecord()
    }
}