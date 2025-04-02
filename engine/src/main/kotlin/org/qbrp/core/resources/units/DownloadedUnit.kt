package org.qbrp.core.resources.units
import okhttp3.OkHttpClient
import okhttp3.Request
import org.qbrp.core.resources.ServerResources
import java.io.File
import java.nio.file.Path
import org.qbrp.core.resources.data.ZipData
import java.util.zip.ZipFile

class DownloadedUnit(
    path: Path,
    name: String,
    extension: String,
    override val data: ZipData
) : TextUnit(path, name, extension, data) {

    private val client = OkHttpClient()
    val logger = ServerResources.getLogger()

    fun download(url: String) {
        logger.log("Начинается загрузка файла по URL: $url")

        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                logger.error("Ошибка загрузки файла. Код ответа: ${response.code}")
                throw IllegalStateException("Ошибка загрузки: ${response.code}")
            }

            val destinationFile = File(data.destinationPath)
            logger.log("Файл будет сохранён в: ${destinationFile.absolutePath}")

            try {
                destinationFile.outputStream().use { output ->
                    response.body?.byteStream()?.copyTo(output)
                }
                logger.log("Файл успешно загружен: ${destinationFile.absolutePath}")
            } catch (e: Exception) {
                logger.error("Ошибка при сохранении файла: ${e.message}")
                throw IllegalStateException("Не удалось сохранить файл: ${e.message}", e)
            }
        }
    }

    fun extract(destination: Path) {
        val zipFile = File(data.destinationPath)
        if (!zipFile.exists()) throw IllegalStateException("File ${data.destinationPath} not found")

        ZipFile(zipFile).use { zip ->
            zip.entries().asSequence().forEach { entry ->
                val entryDestination = destination.resolve(entry.name)
                if (entry.isDirectory) {
                    entryDestination.toFile().mkdirs()
                } else {
                    entryDestination.parent.toFile().mkdirs()
                    zip.getInputStream(entry).use { input ->
                        entryDestination.toFile().outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                }
            }
        }
        path.toFile().deleteRecursively()
    }

    override fun save() {
        // Метод сохранения реализован, если потребуется
    }
}
