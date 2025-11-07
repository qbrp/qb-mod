package org.qbrp.engine.client.core.resources

import org.qbrp.core.resources.structure.ImagesStructure
import org.qbrp.core.resources.structure.Structure
import org.qbrp.core.resources.units.ContentUnit
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ClientStructure(path: File): Structure(path) {
    private lateinit var chatLogSession: ContentUnit
    val config = open("config.json", ClientConfigData::class.java).data as ClientConfigData
    val mainMenu = addBranch("main_menu")
    val chatLogs = addBranch("chat_logs")

    fun setAutoLoginCode(code: String) {
        config.account.code = code
        save()
    }

    fun getAutoLoginCode(): String {
        return config.account.code
    }

    fun getChatMessagesLog(requiredTotal: Int): List<ChatData.MessageDTO> {
        val logs = mutableListOf<ChatData.MessageDTO>()
        val files = chatLogs.path.toFile().listFiles()?.sortedBy { it.name } ?: emptyList()
        files.forEach { file ->
            val logData = chatLogs.open(file.name, ChatData::class.java).data as ChatData
            logs.addAll(logData.getMessages())
            if (logs.size >= requiredTotal) return logs.takeLast(requiredTotal)
        }
        return logs.takeLast(requiredTotal)
    }

    fun addChatMessageToStorage(message: ChatData.MessageDTO) {
        if (!::chatLogSession.isInitialized) createChatLogSession()
        (chatLogSession.data as ChatData).addMessage(message)
        chatLogSession.save()
    }

    fun createChatLogSession() {
        val time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
        chatLogSession = chatLogs.addUnit(ChatData(), "logs_$time", "json")
    }
}