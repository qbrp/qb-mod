package org.qbrp.core.resources.units
import org.qbrp.core.resources.data.pack.TextureData
import org.qbrp.core.resources.ServerResources
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

class TextureUnit(
    path: Path,
    name: String,
    extension: String,
    data: TextureData,
) : ContentUnit(path, name, "png", data) {

    // Метод для копирования текстуры в новый путь
    override fun save() {
        val sourcePath = Path.of(data.toFile()) // Исходный путь к изображению
        val destinationPath = path // Путь назначения
        try {  Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING)
        } catch (e: Exception) {
            ServerResources.getLogger().error("Ошибка при копировании файла: ${e.message}")
        }
    }
}
