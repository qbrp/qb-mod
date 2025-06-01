package org.qbrp.client.engine.chat.system.logs

import org.koin.core.component.KoinComponent
import org.qbrp.main.core.assets.AssetsAPI
import org.qbrp.main.core.assets.common.SimpleKey
import org.qbrp.main.core.assets.common.references.JsonFileReference
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatLogger(val logFile: File, val assetsAPI: AssetsAPI): KoinComponent {
    private var key = getKey()

    private val fileReference = JsonFileReference<ChatData>(key, ChatData::class)
    val session: ChatData
        get() = assetsAPI.getOrCreate(ChatData(), fileReference)

    fun createSession() {
        key = getKey()
    }

    fun saveSession() {
        fileReference.write(session)
    }

    private fun getKey(): SimpleKey {
        val time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
        return SimpleKey(logFile.resolve("log_$time") )
    }
}