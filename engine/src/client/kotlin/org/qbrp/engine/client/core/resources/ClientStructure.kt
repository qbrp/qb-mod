package org.qbrp.engine.client.core.resources

import config.ClientConfig
import icyllis.modernui.graphics.Image
import org.qbrp.core.resources.structure.ImagesStructure
import org.qbrp.engine.client.core.resources.data.ClientConfigData
import org.qbrp.core.resources.structure.Structure
import org.qbrp.core.resources.units.TextUnit
import org.qbrp.core.resources.units.TextureUnit
import org.qbrp.engine.client.core.resources.data.ChatData
import org.qbrp.engine.client.core.resources.data.MainMenuImagesData
import org.qbrp.system.utils.keys.Key
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ClientStructure(path: File): Structure(path) {
    val config = open("config.json", ClientConfigData::class.java)
    val configData = config.data as ClientConfigData
    val mainMenu = addBranch("main_menu")
    private val mainMenuImages = ImagesStructure("images", mainMenu)
    private val mainMenuImageDescriptions = mainMenu.open("descriptions.json", MainMenuImagesData::class.java).data as MainMenuImagesData

    init {
        mainMenuImages.openImages()
        config.save()
    }

    val chatLogs = addBranch("chat_logs")
    private lateinit var chatLogSession: TextUnit

    fun getMenuImageDescriptions(): List<MainMenuImagesData.ImageDescription> {
        return mainMenuImageDescriptions.images
    }

    fun getMenuImage(index: Int): Image {
        return (mainMenuImages.content(Key("image$index")) as TextureUnit).createMuiImage()
    }

    fun getAutoLoginCode(): String {
        return ClientConfig.accountCode
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