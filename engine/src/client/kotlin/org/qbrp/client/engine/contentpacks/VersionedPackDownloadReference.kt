package org.qbrp.client.engine.contentpacks

import okhttp3.Request
import org.qbrp.main.core.assets.common.references.FilePathReference
import java.io.File

class VersionedPackDownloadReference(
    key: PackDownloadKey,
) : DownloadZipFileReference<ServerPack>(key, { ServerPack(it) }) {
    val existingPack =
        try { FilePathReference<ServerPack>(ServerPackKey(key.path), { ServerPack(it) }).read() }
        catch (e: Exception) { null }
    val versionProvider: (File) -> String = { existingPack?.version ?: "NONE" }

    suspend fun readWithoutRequest(): ServerPack {
        return existingPack ?: readAsync()
    }

    override suspend fun readAsync(): ServerPack {
        try {
            //key.validateHost()
            fun getDownloadUrl(host: String, request: String): String {
                return "http://$host/$request"
            }
            val reqUrl = getDownloadUrl((key as PackDownloadKey).serverHost, "update/checkVersion/${versionProvider(path)}")
            val request = Request.Builder()
                .url(reqUrl)  // Укажи нужный URL
                .get()
                .build()

            val response = HTTP_CLIENT.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    LOGGER.log("Ошибка получения версии: ${response.code}")
                    throw RuntimeException("Versioning error for ${reqUrl}: ${response.code}")
                } else {
                    response.body?.string()
                }
            }
            if (response == "up-to-date") {
                return existingPack!!
            } else {
                LOGGER.log("Получена ссылка скачивания ресурсов: $response")
                download("${key.downloadUrl}/$response")
                return factory(path)
            }
        } catch (e: Exception) {
            if (existingPack != null) {
                return existingPack
            } else {
                throw e
            }
        }
    }
}