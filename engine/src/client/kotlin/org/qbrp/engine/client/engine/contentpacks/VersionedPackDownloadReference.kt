package org.qbrp.engine.client.engine.contentpacks

import okhttp3.Request
import org.qbrp.core.assets.common.files.FilePathReference
import org.qbrp.engine.client.EngineClient
import org.qbrp.engine.client.core.assets.DownloadKey
import org.qbrp.engine.client.core.assets.DownloadZipFileReference
import java.io.File
import java.net.NoRouteToHostException

class VersionedPackDownloadReference(
    key: PackDownloadKey,
) : DownloadZipFileReference<ServerPack>(key, { ServerPack(it) }) {
    val existingPack =
        try { FilePathReference<ServerPack>(ServerPackKey(key.serverName), { ServerPack(it) }).read() }
        catch (e: Exception) { null }
    val versionProvider: (File) -> String = { existingPack?.version ?: "NONE" }

    suspend fun readWithoutRequest(): ServerPack {
        return existingPack ?: readAsync()
    }

    override suspend fun readAsync(): ServerPack {
        key as PackDownloadKey
        try {
            //key.validateHost()
            fun getDownloadUrl(host: String, request: String): String {
                return "http://$host/$request"
            }

            val reqUrl = getDownloadUrl(key.serverHost, "update/checkVersion/${versionProvider(path)}")
            val request = Request.Builder()
                .url(reqUrl)  // Укажи нужный URL
                .get()
                .build()

            val response = CLIENT.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    println("Ошибка получения версии: ${response.code}")
                    throw RuntimeException("Versioning error for ${reqUrl}: ${response.code}")
                } else {
                    response.body?.string()
                }
            }
            if (response == "up-to-date") {
                return existingPack!!
            } else {
                download("${key.downloadUrl}/$response")
                return factory(path)
            }
        } catch (e: Exception) {
            val msg = when (e) {
                is NoRouteToHostException -> "Хост ${key.downloadUrl} недоступен"
                else -> e.localizedMessage
            }
            if (existingPack != null) {
                return existingPack
            } else {
                EngineClient.notificationsManager.sendSystemMessage("Ошибка загрузки", e.message.toString())
                throw e
            }
        }
    }
}