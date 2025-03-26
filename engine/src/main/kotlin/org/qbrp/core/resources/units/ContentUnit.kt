package org.qbrp.core.resources.units
import org.qbrp.core.resources.Savable
import org.qbrp.core.resources.data.Data
import java.nio.file.Path

open class ContentUnit(
    path: Path,
    name: String,
    extension: String,
    open val data: Data
) : Unit(path.resolve("$name.$extension") as Path), Savable {
    var autoOverwrite: Boolean = false

    override fun initFile(): Unit {
        val filePath = this.path.toFile()
        if (!filePath.exists()) {
            try { filePath.createNewFile()
            } catch (e: Exception) {
                throw IllegalStateException("Не удалось создать файл: ${filePath.path}", e)
            }
        }
        if (autoOverwrite) save()
        return this
    }

    override fun save() {
        path.toFile().writeText(data.toFile())
    }

}
