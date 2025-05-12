package org.qbrp.engine.client.core.assets

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.qbrp.core.assets.FileSystem
import org.qbrp.core.assets.FileSystem.getOrCreate
import org.qbrp.core.assets.common.Asset
import org.qbrp.core.assets.common.files.AsyncFileReference
import org.qbrp.system.utils.log.Loggers
import java.io.File
import java.nio.file.Path
import java.util.concurrent.TimeUnit
import java.util.zip.ZipFile
import kotlin.sequences.forEach

open class DownloadZipFileReference<T : Asset>(
    override val key: DownloadKey,
    protected open val factory: (File) -> T
) : AsyncFileReference<T> {

    protected val path = FileSystem.getOrCreate(File(key.getId()), true)

    override fun exists(): Boolean = true

    companion object {
        internal val LOGGER = Loggers.get("downloading")
        internal val CLIENT = OkHttpClient.Builder()
            .callTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(5, TimeUnit.SECONDS)
            .build()
    }

    override suspend fun readAsync(): T {
        download(key.downloadUrl)
        return factory(path)
    }

    suspend fun download(url: String) = withContext(Dispatchers.IO) {
        //key.validateHost()
        LOGGER.log("Начинается загрузка файла по URL: ${url}")

        val request = Request.Builder().url(url).build()
        CLIENT.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                LOGGER.error("Ошибка загрузки файла. Код ответа: ${response.code}")
                throw IllegalStateException("Ошибка загрузки: ${response.code}")
            }

            val zipPath = File("$path.zip")
            LOGGER.log("Файл будет сохранён в: ${zipPath.absolutePath}")

            try {
                zipPath.outputStream().use { output ->
                    response.body?.byteStream()?.copyTo(output)
                }
                extract(zipPath, path)
                LOGGER.log("Файл успешно загружен: ${path.absolutePath}")
            } catch (e: Exception) {
                LOGGER.error("Ошибка при сохранении файла: ${e.message}")
                throw IllegalStateException("Couldn't save the file: ${e.message}", e)
            }
        }
    }

    fun extract(zipFile: File, destination: File) {
        if (!zipFile.exists()) {
            throw IllegalStateException("File $zipFile not found")
        }

        ZipFile(zipFile).use { zip ->
            zip.entries().asSequence().forEach { entry ->
                val outFile = File(destination, entry.name)
                if (entry.isDirectory) {
                    outFile.mkdirs()
                    return@forEach
                }
                outFile.parentFile?.let { parent ->
                    if (!parent.exists()) parent.mkdirs()
                }
                zip.getInputStream(entry).use { input ->
                    outFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }
        }
    }


    override fun write(data: T) {
        throw UnsupportedOperationException()
    }
}
